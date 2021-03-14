module learn.boot {
	
	opens com.example.demo;
	opens com.example.demo.api;
	
	requires java.persistence;
	requires java.validation;
	
	requires static lombok;
	requires slf4j.api;
	requires org.mockito;
	
	requires spring.boot;
	requires spring.boot.autoconfigure;
	requires spring.context;
	requires spring.data.commons;
	requires spring.web;
	requires spring.core;
	requires spring.beans;
	requires spring.aspects;
	requires spring.boot.test.autoconfigure;
}