package com.example.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.sleuth.instrument.async.TraceableExecutorService;
import org.springframework.stereotype.Service;

/**
 * @author Muneer Ahmed
 * @version 1.0
 * @since 2018-09-10
 */
@Slf4j
@Service
public class ConcurrentExecutor {

    @Value("${concurrent.executor.corePoolSize:100}")
    private int corePoolSize;

    @Value("${concurrent.executor.maximumPoolSize:100}")
    private int maximumPoolSize;

    @Value("${concurrent.executor.keepAliveTimeSeconds:0}")
    private int keepAliveTimeSeconds;

    private static final String X_LABEL = "X-Label";

    @Autowired
    private BeanFactory beanFactory;

    private ExecutorService executorService;

    @PostConstruct
    void postConstruct() {
        executorService = new TraceableExecutorService(beanFactory,
                new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTimeSeconds, TimeUnit.SECONDS,
                        new LinkedBlockingQueue<>()));
    }

    public <T> List<Future<T>> execute(String label, List<Callable<T>> tasks) {
        try {
            List<Callable<T>> traceableTasks = new ArrayList<>();
            tasks.forEach(e -> traceableTasks.add(new TraceableCallable<>(label, e)));
            return executorService.invokeAll(traceableTasks);
        } catch (InterruptedException excp) {
            log.error("Error in Concurrent Execution", excp);
            throw new RuntimeException(excp);
        }
    }

    class TraceableCallable<V> implements Callable<V> {

        protected TraceableCallable(String label, Callable<V> delegate) {
            this.label = label;
            this.delegate = delegate;
        }

        private final String label;
        private final Callable<V> delegate;

        @Override
        public V call() throws Exception {
            try {
                MDC.put(X_LABEL, label);
                return delegate.call();
            } finally {
                MDC.remove(X_LABEL);
            }
        }
    }
}
