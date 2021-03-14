module learn.boot {
	
	opens com.example.demo;
	opens com.example.demo.api;
    
	requires java.persistence;
	requires java.annotation;
	requires java.sql;
	requires java.validation;

	requires spring.boot;
	requires spring.boot.autoconfigure;
	requires spring.context;
	requires spring.boot.actuator;
	requires spring.boot.actuator.autoconfigure;

	requires spring.core;
	requires spring.beans;
	requires spring.aspects;
	
	requires spring.jdbc;
	requires spring.orm;
	requires spring.tx;
	
	requires spring.data.jpa;
	requires spring.data.commons;
	requires spring.data.mongodb;
	requires spring.data.rest.core;
	requires spring.data.redis;

	requires spring.web;
	requires spring.webmvc;
	requires spring.webflux;

	requires spring.security.web;
	requires spring.security.core;
	requires spring.security.config;
	requires spring.security.oauth2.core;
	requires spring.security.oauth2.client;
	requires spring.security.ldap;
	requires spring.ldap.core;
		
	requires keycloak.spring.boot.adapter.core;
	requires keycloak.spring.security.adapter;

	requires org.mongodb.driver.core;
	requires org.mongodb.driver.sync.client;
	
	requires lucene.core;
	requires lucene.queryparser;

	requires spring.data.solr;
	requires solr.solrj;
	
//	requires org.hibernate.search.engine;
//	requires org.hibernate.search.orm;

	requires brave;
	requires spring.cloud.sleuth.instrumentation;
	requires spring.cloud.sleuth.api;

	requires static lombok;
	requires org.slf4j;
	requires com.fasterxml.jackson.annotation;
	requires org.apache.commons.lang3;
	requires org.apache.tomcat.embed.core;

	requires graphql.java.tools;
	requires reactor.core;
	requires org.reactivestreams;
}