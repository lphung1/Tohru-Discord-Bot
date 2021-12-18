package Util;

import java.util.function.Consumer;

public final class RunnableCallback<T> implements Runnable {

    Consumer<T> consumer;
    T param;

    public RunnableCallback(Consumer<T> consumer, T param) {
        this.consumer = consumer;
        this.param = param;
    }

    @Override
    public void run() {
        consumer.accept(param);
    }
}
