package com.example.demo.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
/**
 * This is optional : needed only when one wants to use specific SolrTemplate per core instead of generic one with core as argument.
 * Both the templates are used in CmdRunner.java
 * 
 * @author amit.30.kumar
 */
@Configuration
@EnableSolrRepositories(basePackages = "com.example.demo.data.search.solr", namedQueriesLocation = "classpath:solr-named-queries.properties")
public class SolrConfiguration {

	@Value("${spring.data.solr.host:http://localhost:8983/solr}")
	private String SOLR_HOST;

	@Bean
	public SolrClient solrClient() {
		return new HttpSolrClient.Builder(SOLR_HOST).build();
	}

	@Bean
	public SolrTemplate solrTemplate(SolrClient client) throws Exception {
		return new SolrTemplate(solrClient());
	}

	// A custom template for specific core will do instead of previous `multicoreSupport = true` in @EnableSolrRepositories
	@Bean
	public SolrTemplate core1Template() {
		return new SolrTemplate(new HttpSolrClient.Builder(SOLR_HOST + "/my_core").build());
	}
}
