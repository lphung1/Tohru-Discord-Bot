#!/bin/sh

# This will have this script check for a new version of JMusicBot every
# startup (and download it if the latest version isn't currently downloaded)
DOWNLOAD=true

# This will cause the script to run in a loop so that the bot auto-restarts
# when you use the shutdown command
LOOP=false

download() {
    if [ $DOWNLOAD == true ]; then
        URL=$(curl -s https://api.github.com/repos/lphung1/Tohru-Discord-Bot/releases/latest \
           | grep -i browser_download_url.*\.jar \
           | sed 's/.*\(http.*\)"/\1/')
        FILENAME=$(echo $URL | sed 's/.*\/\([^\/]*\)/\1/')
        if [ -f $FILENAME ]; then
            echo "Latest version already downloaded (${FILENAME})"
        else
            curl -L $URL -o $FILENAME
        fi
    fi
}

run() {
    java -jar $(ls -t Tohru_Discord_Bot* | head -1)
}

while
    download
    run
    $LOOP
do
    continue
done