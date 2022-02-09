package Util;

import org.javacord.api.event.message.MessageCreateEvent;

import java.util.concurrent.Callable;

public final class CallOrPrint<T> implements Callable {

    Callable<T> callable;
    MessageCreateEvent messageCreateEvent;

    public CallOrPrint(Callable<T> callable, MessageCreateEvent messageCreateEvent) {
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
