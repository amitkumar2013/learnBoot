package com.example.demo.config;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.sleuth.instrument.async.LazyTraceExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * This enables scheduler with cron like syntax and is switched off by default.
 * 
 * @author amit.30.kumar
 */
@Configuration

@EnableAsync
//add the infrastructure role to ensure that the bean gets auto-proxied
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)

@EnableScheduling

// This is excluded in test as @SpringBootTest(properties = "io.reflectoring.scheduling.enabled=false")
@ConditionalOnProperty(name = "io.reflectoring.scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class SchedulingConfiguration {

	@Autowired
	private BeanFactory beanFactory;
	boolean traceEnabled = true;

	// For @EnableAsync with Trace Span Concept - @see MiscService
	@Bean("threadPoolExecutor")
	public Executor asyncTaskExecutor() {
		// SimpleAsyncTaskExecutor, SyncTaskExecutor, TimerTaskExecutor etc. - use appropriately
		ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setCorePoolSize(1);
		threadPoolTaskExecutor.setMaxPoolSize(1);
		threadPoolTaskExecutor.setQueueCapacity(500);
		threadPoolTaskExecutor.setThreadNamePrefix("Async-worker-");
		threadPoolTaskExecutor.initialize();
		// Special kind of executor that will propagate traceIds to new threads and create new spanIds in the process.
		return traceEnabled ? new LazyTraceExecutor(beanFactory, threadPoolTaskExecutor) : threadPoolTaskExecutor;
	}
}