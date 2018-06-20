package io.systom.coin.utils;

import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class TaskFuture<T> {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(TaskFuture.class);
    private LinkedBlockingQueue<T> queue;

    public TaskFuture() {
        queue = new LinkedBlockingQueue(1);
    }

    public void offer(T e) {
        try {
            queue.put(e);
        } catch (InterruptedException ex) {
            logger.error("", ex);
        }
    }

    public boolean isDone() {
        return queue.size() > 0;
    }

    public T take() throws InterruptedException, ExecutionException {
        return queue.take();
    }

    public T poll(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return queue.poll(timeout, unit);
    }
}
