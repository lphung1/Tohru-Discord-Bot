package Util;

import org.javacord.api.event.message.MessageCreateEvent;

import java.util.concurrent.Callable;

public final class CallableOrPrint<T> implements Callable {

    Callable<T> callable;
    MessageCreateEvent messageCreateEvent;

    public CallableOrPrint(Callable<T> callable, MessageCreateEvent messageCreateEvent) {
        this.callable = callable;
        this.messageCreateEvent = messageCreateEvent;
    }

    @Override
    public T call() {
        try {
            return callable.call();
        } catch (Exception e) {
            MessageUtil.printStackTrace(messageCreateEvent, e);
            return null;
        }
    }

}
