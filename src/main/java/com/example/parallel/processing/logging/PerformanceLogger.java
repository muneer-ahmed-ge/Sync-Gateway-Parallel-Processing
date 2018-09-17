package com.example.parallel.processing.logging;

import brave.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author Muneer Ahmed
 * @version 1.0
 * @since 2018-08-12
 */
@Slf4j
@Aspect
@Configuration
@EnableAspectJAutoProxy
public class PerformanceLogger {

    @Autowired
    public PerformanceLogger(Tracer tracer) {
        this.tracer = tracer;
    }

    private final Tracer tracer;

    @Bean
    public PerformanceLoggingInterceptor loggingInterceptor() {
        return new PerformanceLoggingInterceptor(false, tracer);
    }

    @Bean
    public Advisor loggingAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("com.example.parallel.processing.logging.PerformanceLogger.monitor()");
        return new DefaultPointcutAdvisor(pointcut, loggingInterceptor());
    }

    @Pointcut(
            "(execution(* com.example.parallel.processing.controller.FactorialController.factorial(..)) || " +
                    "execution(* com.example.parallel.processing.service.FactorialService.calculate(..)) )"
    )
    public void monitor() {
    }

}
