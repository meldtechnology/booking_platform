package com.jade.platform.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * Aspect for collecting metrics on service operations.
 * <p>
 * This aspect collects metrics for all methods in the service layer, including:
 * - Method execution counts
 * - Method execution times
 * - Success/failure counts
 * </p>
 * <p>
 * The metrics are tagged with the service name, method name, and result (success/failure).
 * </p>
 */
@Aspect
@Component
public class ServiceMetricsAspect {
    private static final Logger log = LoggerFactory.getLogger(ServiceMetricsAspect.class);
    
    private final MeterRegistry meterRegistry;
    
    /**
     * Creates a new ServiceMetricsAspect with the given MeterRegistry.
     *
     * @param meterRegistry the meter registry to record metrics
     */
    public ServiceMetricsAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        log.info("Service metrics aspect initialized");
    }
    
    /**
     * Pointcut for all methods in the service layer.
     */
    @Pointcut("execution(* com.jade.platform.service.*.*(..))")
    public void serviceOperation() {
    }
    
    /**
     * Collects metrics for service operations.
     * <p>
     * This method intercepts all service operations and records:
     * - The execution time
     * - The number of invocations
     * - The number of successful/failed invocations
     * </p>
     *
     * @param joinPoint the join point for the intercepted method
     * @return the result of the method execution
     * @throws Throwable if the method execution throws an exception
     */
    @Around("serviceOperation()")
    public Object collectMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.getName();
        String className = method.getDeclaringClass().getSimpleName();
        
        // Create a timer for the method execution
        Timer.Sample sample = Timer.start(meterRegistry);
        
        // Create counters for success and failure
        Counter successCounter = Counter.builder("service.invocation.success")
                .tag("class", className)
                .tag("method", methodName)
                .description("Number of successful service invocations")
                .register(meterRegistry);
        
        Counter failureCounter = Counter.builder("service.invocation.failure")
                .tag("class", className)
                .tag("method", methodName)
                .description("Number of failed service invocations")
                .register(meterRegistry);
        
        try {
            // Execute the method
            Object result = joinPoint.proceed();
            
            // Record success
            successCounter.increment();
            
            // Record execution time
            sample.stop(Timer.builder("service.execution.time")
                    .tag("class", className)
                    .tag("method", methodName)
                    .tag("result", "success")
                    .description("Service method execution time")
                    .register(meterRegistry));
            
            return result;
        } catch (Throwable t) {
            // Record failure
            failureCounter.increment();
            
            // Record execution time
            sample.stop(Timer.builder("service.execution.time")
                    .tag("class", className)
                    .tag("method", methodName)
                    .tag("result", "failure")
                    .tag("exception", t.getClass().getSimpleName())
                    .description("Service method execution time")
                    .register(meterRegistry));
            
            throw t;
        }
    }
}