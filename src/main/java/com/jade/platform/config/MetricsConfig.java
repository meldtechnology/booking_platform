package com.jade.platform.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;

/**
 * Configuration class for metrics.
 * <p>
 * This class configures the metrics infrastructure for the application using Micrometer.
 * It sets up various metrics binders for JVM, system, and application-specific metrics.
 * </p>
 */
@Configuration
public class MetricsConfig {
    private static final Logger log = LoggerFactory.getLogger(MetricsConfig.class);
    
    /**
     * Customizes the meter registry with common tags.
     * <p>
     * This method adds common tags to all metrics, such as the application name,
     * environment, and host name. These tags help identify the source of metrics
     * in monitoring systems.
     * </p>
     *
     * @param environment the Spring environment
     * @return a customizer for the meter registry
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags(Environment environment) {
        final String applicationName = environment.getProperty("spring.application.name", "booking-platform");
        String environmentName = Arrays.toString(environment.getActiveProfiles());
        if (environmentName.equals("[]")) {
            environmentName = "default";
        }
        
        final String finalEnvironmentName = environmentName;
        
        log.info("Configuring metrics with application name: {}, environment: {}", applicationName, finalEnvironmentName);
        
        return registry -> registry.config()
                .commonTags(
                        Tags.of(
                                Tag.of("application", applicationName),
                                Tag.of("environment", finalEnvironmentName)
                        )
                );
    }
    
    /**
     * Creates a TimedAspect for the @Timed annotation.
     * <p>
     * This bean enables the @Timed annotation, which can be used to measure
     * the execution time of methods.
     * </p>
     *
     * @param registry the meter registry
     * @return a TimedAspect
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
    
    /**
     * Registers JVM memory metrics.
     * <p>
     * These metrics provide information about heap and non-heap memory usage.
     * </p>
     *
     * @return a JvmMemoryMetrics binder
     */
    @Bean
    public JvmMemoryMetrics jvmMemoryMetrics() {
        JvmMemoryMetrics metrics = new JvmMemoryMetrics();
        metrics.bindTo(Metrics.globalRegistry);
        return metrics;
    }
    
    /**
     * Registers JVM garbage collection metrics.
     * <p>
     * These metrics provide information about garbage collection events and durations.
     * </p>
     *
     * @return a JvmGcMetrics binder
     */
    @Bean
    public JvmGcMetrics jvmGcMetrics() {
        JvmGcMetrics metrics = new JvmGcMetrics();
        metrics.bindTo(Metrics.globalRegistry);
        return metrics;
    }
    
    /**
     * Registers JVM thread metrics.
     * <p>
     * These metrics provide information about thread counts and states.
     * </p>
     *
     * @return a JvmThreadMetrics binder
     */
    @Bean
    public JvmThreadMetrics jvmThreadMetrics() {
        JvmThreadMetrics metrics = new JvmThreadMetrics();
        metrics.bindTo(Metrics.globalRegistry);
        return metrics;
    }
    
    /**
     * Registers class loader metrics.
     * <p>
     * These metrics provide information about class loading and unloading.
     * </p>
     *
     * @return a ClassLoaderMetrics binder
     */
    @Bean
    public ClassLoaderMetrics classLoaderMetrics() {
        ClassLoaderMetrics metrics = new ClassLoaderMetrics();
        metrics.bindTo(Metrics.globalRegistry);
        return metrics;
    }
    
    /**
     * Registers processor metrics.
     * <p>
     * These metrics provide information about CPU usage.
     * </p>
     *
     * @return a ProcessorMetrics binder
     */
    @Bean
    public ProcessorMetrics processorMetrics() {
        ProcessorMetrics metrics = new ProcessorMetrics();
        metrics.bindTo(Metrics.globalRegistry);
        return metrics;
    }
    
    /**
     * Registers uptime metrics.
     * <p>
     * These metrics provide information about the application's uptime.
     * </p>
     *
     * @return an UptimeMetrics binder
     */
    @Bean
    public UptimeMetrics uptimeMetrics() {
        UptimeMetrics metrics = new UptimeMetrics();
        metrics.bindTo(Metrics.globalRegistry);
        return metrics;
    }
}