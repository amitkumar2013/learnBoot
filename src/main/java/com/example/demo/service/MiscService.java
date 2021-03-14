package com.example.demo.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.sleuth.annotation.NewSpan;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.demo.data.rest.ProductRepo;
import com.example.demo.data.rest.model.Product;

import lombok.extern.slf4j.Slf4j;

/**
 * The most basic service. 
 * 
 * Showcases Caching for a DB Model, usually done at API level.
 * 
 * @author amit.30.kumar
 */
@Service
@Slf4j
public class MiscService {

	public String greet(String name) {
		return "Hello " + name.toUpperCase() + "!";
	}


	/*
	 * Trace using zipkins with Spans. Here is a sample for manually creating new Span.
	 * 
	 * // Old School
	 * @Autowired private Tracer tracer;...
	 * Span newSpan = tracer.nextSpan().name("new-span").start();
	 * 	try (SpanInScope ws = tracer.withSpanInScope(newSpan.start())) {
	 * 		log.info("New span needs a START and CLOSE!");
	 * 	} finally {
	 * 		newSpan.finish();
	 * }
	 */
	@NewSpan("custom-new-span")
    public void someWorkInNewTracingSpan() throws InterruptedException {
        log.info("In the new span");
    }
    
	// ----------------------------------------------------------------------------
    /*
     * Scheduling - @EnableScheduling & Async - @EnableAsync 
     */
	@Async
	@Scheduled(fixedDelay = 90000, initialDelay = 1000) // Can also move to config - "${fixedRate.in.milliseconds}" 
	// @Scheduled(cron = "0 15 10 15 * ?", zone = "Europe/Paris")
	public void scheduledWork() throws InterruptedException {
		log.info("Start some work from the scheduled task");
		Thread.sleep(1000L);
		log.info("End work from scheduled task");
	}
	 
	// ----------------------------------------------------------------------------
	@Autowired
	private ProductRepo productRepository;

	/*
	 * Cache in 'users' with key 'name' the parameter; a custom keyGenerator can
	 * also be used
	 * 
	 * cacheManager & cacheResolver - in case of multiple
	 * 
	 * condition & unless - for if/else
	 */
	@Cacheable(value = "products", key = "#prodId")
	public Optional<Product> getProduct(Long prodId) {
	  log.info("Getting product with ID {}.", prodId);
	  return productRepository.findById(prodId);
	}

	@CachePut(value = "products", key = "#product.id")
	public Product updateProduct(Product product) {
		productRepository.save(product);
		return product;
	}
	
	@CacheEvict(value = "products", allEntries=true)
	public void deleteProductByID(Long id) {
	  log.info("deleting product with id {}", id);
	  productRepository.deleteById(id);
	}	
}
