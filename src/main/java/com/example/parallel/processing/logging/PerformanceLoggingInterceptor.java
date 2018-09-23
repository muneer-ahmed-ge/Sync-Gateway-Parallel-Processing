package com.example.parallel.processing.logging;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;

import brave.Tracer;
import com.example.parallel.processing.service.FactorialService;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.springframework.aop.interceptor.AbstractMonitoringInterceptor;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 *
 *
 *
 *
 * @author Muneer Ahmed
 * @version 1.0
 * @since 2018-08-12
 */
class PerformanceLoggingInterceptor extends AbstractMonitoringInterceptor {

    private final String CONTROLLER = "com.example.parallel.processing.controller";

    private final Tracer tracer;

    public PerformanceLoggingInterceptor(boolean useDynamicLogger, Tracer tracer) {
        this.tracer = tracer;
        setUseDynamicLogger(useDynamicLogger);
    }

    private final ConcurrentHashMap<String, Long> platformTime = new ConcurrentHashMap();

    @Override
    protected Object invokeUnderTrace(MethodInvocation invocation, Log log)
            throws Throwable {
        String name = createInvocationTraceName(invocation);
        long start = Instant.now().toEpochMilli();
        try {
            return invocation.proceed();
        } finally {
            switch (getType(invocation)) {
                case 0:
                    handleController(start, log);
                    break;
                case 1:
                    platform(tracer, start, log);
                    break;
                default:
                    break;
            }
            // log.info("Method " + name + " execution lasted:" + (Instant.now().toEpochMilli() - start) + " ms");
        }
    }

    private int getType(MethodInvocation invocation) {
        int result = -1;
        if (invocation.getThis().getClass().getPackage().getName().equalsIgnoreCase(CONTROLLER)) {
            result = 0;
        } else if (invocation.getThis().getClass().getName().equalsIgnoreCase(FactorialService.class.getName())) {
            result = 1;
        }
        return result;
    }

    private void handleController(long start, Log log) {
        String traceId = tracer.currentSpan().context().traceIdString();
        HttpServletRequest request = getCurrentRequest();
        long totalExecutionTime = Instant.now().toEpochMilli() - start;
        long platform = PerformanceNodeHelper.getTotalTime(traceId, platformTime);
        log.debug("platformTime: " + platformTime);
        long internal = totalExecutionTime - platform;
        long internalPercentage = (internal * 100) / totalExecutionTime;
        long platformPercentage = (platform * 100) / totalExecutionTime;
        log.trace("Method:" + request.getMethod() + " Path:" + request.getServletPath()
                + " Total Execution Time:" + totalExecutionTime + " ms " +
                " Internal:" + internal + " ms [" + internalPercentage + "%]" +
                " Platform:" + platform + " ms[" + platformPercentage + "%]");
    }

    private void platform(Tracer tracer, long start, Log log) {
        long executionTime = Instant.now().toEpochMilli() - start;
        String key = tracer.currentSpan().context().toString();
        platformTime.put(key, executionTime);
        log.debug("recorded key=" + key + " exec=" + executionTime);
    }


    private HttpServletRequest getCurrentRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Assert.state(requestAttributes != null, "Could not find current request via RequestContextHolder");
        Assert.isInstanceOf(ServletRequestAttributes.class, requestAttributes);
        HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
        Assert.state(servletRequest != null, "Could not find current HttpServletRequest");
        return servletRequest;
    }

}
