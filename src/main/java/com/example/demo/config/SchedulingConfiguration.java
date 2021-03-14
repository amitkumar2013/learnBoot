package com.example.demo.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
// This is excluded in test as @SpringBootTest(properties = "io.reflectoring.scheduling.enabled=false")
@ConditionalOnProperty(name = "io.reflectoring.scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class SchedulingConfiguration {
}