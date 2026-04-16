package com.sling.hotel.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncTaskExecutor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class VirtualThreadExecutor implements AsyncTaskExecutor {

    private static final Logger log = LoggerFactory.getLogger(VirtualThreadExecutor.class);

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    public void execute(Runnable task) {
        executor.execute(() -> {
            try {
                task.run();
            } catch (Exception ex) {
                log.error("Error executing task in virtual thread", ex);
            }
        });
    }

    @Override
    public Future<?> submit(Runnable task) {
        return executor.submit(task);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return executor.submit(task);
    }
}
