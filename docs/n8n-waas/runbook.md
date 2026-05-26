# n8n WaaS — Runbook: Hostinger + CloudFront Setup

## Prerequisites
- Hostinger VPS provisioned (Ubuntu 22.04)
- Static IP assigned in Hostinger panel
- Phase 1 of plan.md complete (RDS, S3, IAM ready)
- Domain DNS access

---

## 1. Secure the VPS

```bash
# SSH in
ssh root@HOSTINGER_IP

# Update system
apt update && apt upgrade -y

# Create deploy user
useradd -m -s /bin/bash deploy
usermod -aG sudo deploy
mkdir -p /home/deploy/.ssh
cp ~/.ssh/authorized_keys /home/deploy/.ssh/
chown -R deploy:deploy /home/deploy/.ssh

# Harden SSH
sed -i 's/PermitRootLogin yes/PermitRootLogin no/' /etc/ssh/sshd_config
sed -i 's/#PasswordAuthentication yes/PasswordAuthentication no/' /etc/ssh/sshd_config
systemctl restart sshd

# Firewall
ufw allow 22/tcp
ufw allow 80/tcp
ufw allow 443/tcp
ufw --force enable
```

Re-login as deploy user before continuing.

---

## 2. Install Docker

```bash
# Install Docker
curl -fsSL https://get.docker.com | sh
usermod -aG docker deploy
newgrp docker

# Install Docker Compose plugin
apt install -y docker-compose-plugin

# Verify
docker --version
docker compose version
```

---

## 3. Install Certbot (SSL)

```bash
apt install -y certbot python3-certbot-nginx

# Stop anything on port 80 first, then get cert
certbot certonly --standalone -d DOMAIN --non-interactive --agree-tos -m admin@DOMAIN

# Verify cert created
ls /etc/letsencrypt/live/DOMAIN/
```

---

## 4. Create App Directory and Config Files

```bash
mkdir -p /opt/n8n
cd /opt/n8n
```

### 4a. Create `.env`

```bash
cat > /opt/n8n/.env <<EOF
N8N_ENCRYPTION_KEY=<N8N_ENCRYPTION_KEY>
WEBHOOK_URL=https://DOMAIN

# AWS RDS
RDS_ENDPOINT=<rds endpoint from AWS console>
RDS_USER=n8nadmin
RDS_PASSWORD=<RDS_PASSWORD>

# AWS credentials
AWS_ACCESS_KEY_ID=<AWS_ACCESS_KEY_ID>
AWS_SECRET_ACCESS_KEY=<AWS_SECRET_ACCESS_KEY>
AWS_REGION=us-east-1
S3_BUCKET=n8n-waas-storage
S3_BACKUP_BUCKET=n8n-waas-backups
EOF

chmod 600 /opt/n8n/.env
```

### 4b. Create `docker-compose.yml`

```bash
cat > /opt/n8n/docker-compose.yml <<'EOF'
services:

  n8n-main:
    image: n8nio/n8n
    restart: always
    environment:
      - N8N_ENCRYPTION_KEY=${N8N_ENCRYPTION_KEY}
      - N8N_EXECUTIONS_MODE=queue
      - WEBHOOK_URL=${WEBHOOK_URL}
      - DB_TYPE=postgresdb
      - DB_POSTGRESDB_HOST=${RDS_ENDPOINT}
      - DB_POSTGRESDB_PORT=5432
      - DB_POSTGRESDB_USER=${RDS_USER}
      - DB_POSTGRESDB_PASSWORD=${RDS_PASSWORD}
      - DB_POSTGRESDB_DATABASE=n8n
      - DB_POSTGRESDB_SSL_ENABLED=true
      - N8N_EXTERNAL_STORAGE=s3
      - N8N_EXTERNAL_STORAGE_S3_BUCKET=${S3_BUCKET}
      - N8N_EXTERNAL_STORAGE_S3_REGION=${AWS_REGION}
      - AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}
      - AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}
      - QUEUE_BULL_REDIS_HOST=redis
      - QUEUE_BULL_REDIS_PORT=6379
    depends_on:
      - redis

  n8n-worker:
    image: n8nio/n8n
    restart: always
    command: worker
    environment:
      - N8N_ENCRYPTION_KEY=${N8N_ENCRYPTION_KEY}
      - N8N_EXECUTIONS_MODE=queue
      - DB_TYPE=postgresdb
      - DB_POSTGRESDB_HOST=${RDS_ENDPOINT}
      - DB_POSTGRESDB_PORT=5432
      - DB_POSTGRESDB_USER=${RDS_USER}
      - DB_POSTGRESDB_PASSWORD=${RDS_PASSWORD}
      - DB_POSTGRESDB_DATABASE=n8n
      - DB_POSTGRESDB_SSL_ENABLED=true
      - QUEUE_BULL_REDIS_HOST=redis
      - AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}
      - AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}
    depends_on:
      - redis

  redis:
    image: redis:7-alpine
    restart: always
    volumes:
      - redis_data:/data
    command: redis-server --appendonly yes

  nginx:
    image: nginx:alpine
    restart: always
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf:ro
      - /etc/letsencrypt:/etc/letsencrypt:ro

volumes:
  redis_data:
EOF
```

### 4c. Create `nginx.conf`

```bash
cat > /opt/n8n/nginx.conf <<'EOF'
events { worker_connections 1024; }

http {
  server {
    listen 80;
    server_name DOMAIN;
    return 301 https://$host$request_uri;
  }

  server {
    listen 443 ssl;
    server_name DOMAIN;

    ssl_certificate     /etc/letsencrypt/live/DOMAIN/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/DOMAIN/privkey.pem;

    # Only allow CloudFront IPs (optional but recommended)
    # Add CloudFront IP ranges from: https://ip-ranges.amazonaws.com/ip-ranges.json

    location / {
      proxy_pass         http://n8n-main:5678;
      proxy_http_version 1.1;
      proxy_set_header   Upgrade $http_upgrade;
      proxy_set_header   Connection "upgrade";
      proxy_set_header   Host $host;
      proxy_set_header   X-Real-IP $remote_addr;
      proxy_read_timeout 88400s;
    }
  }
}
EOF
```

Replace `DOMAIN` in nginx.conf with your actual domain:
```bash
sed -i 's/DOMAIN/yourdomain.com/g' /opt/n8n/nginx.conf
```

---

## 5. Start the Stack

```bash
cd /opt/n8n
docker compose --env-file .env up -d

# Verify all containers running
docker compose ps

# Check logs for errors
docker compose logs n8n-main --tail=50
docker compose logs n8n-worker --tail=50
```

**Expected:** all 4 containers show `Up`.

---

## 6. Verify RDS Connection

```bash
docker compose logs n8n-main 2>&1 | grep -i "database\|error\|connected"
```

**Expected:** no connection errors, n8n starts successfully.

---

## 7. SSL Cert Auto-Renewal

```bash
# Add cron for cert renewal + nginx reload
crontab -e
# Add this line:
0 3 * * * certbot renew --quiet && docker exec $(docker ps -qf name=nginx) nginx -s reload
```

---

## 8. Setup Postgres Backup to S3

```bash
cat > /opt/n8n/backup.sh <<'EOF'
#!/bin/bash
DATE=$(date +%Y-%m-%d-%H%M)
PGPASSWORD=$RDS_PASSWORD pg_dump \
  -h $RDS_ENDPOINT \
  -U $RDS_USER \
  -d n8n \
  --no-password \
  | gzip \
  | aws s3 cp - s3://$S3_BACKUP_BUCKET/postgres/$DATE.sql.gz

echo "Backup completed: $DATE"
EOF

chmod +x /opt/n8n/backup.sh

# Install aws cli
apt install -y awscli

# Configure aws credentials
aws configure set aws_access_key_id $AWS_ACCESS_KEY_ID
aws configure set aws_secret_access_key $AWS_SECRET_ACCESS_KEY
aws configure set default.region us-east-1

# Add to cron — daily at 2am
crontab -e
# Add this line:
0 2 * * * /bin/bash -a /opt/n8n/.env /opt/n8n/backup.sh >> /var/log/n8n-backup.log 2>&1
```

---

## 9. Verify End-to-End

```bash
# From local machine
curl -I https://DOMAIN

# Expected: HTTP/2 200 or 301 redirect handled
# Check n8n UI loads in browser at https://DOMAIN
```

---

## Operational Commands

```bash
# View logs
docker compose -f /opt/n8n/docker-compose.yml logs -f

# Update n8n to latest
cd /opt/n8n && docker compose pull && docker compose up -d

# Scale workers up
docker compose up -d --scale n8n-worker=4

# Restart single service
docker compose restart n8n-main

# Stop everything
docker compose down

# Check disk usage
df -h
docker system df
```

---

## Monitoring Checks (run weekly)

```bash
# Disk space
df -h /

# Container health
docker compose ps

# Recent errors
docker compose logs --since 24h 2>&1 | grep -i error

# Backup verification
aws s3 ls s3://S3_BACKUP_BUCKET/postgres/ | tail -5
```
