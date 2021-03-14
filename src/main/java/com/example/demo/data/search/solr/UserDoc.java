package com.example.demo.data.search.solr;

import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

// Name of the lucene core
@SolrDocument(collection = "my_core")
@Getter
@Setter
@ToString
public class UserDoc {

	@Id
	@Indexed
	private String id;

	@Indexed(name = "username", type = "string")
	private String username;

	@Indexed(name = "email", type = "string")
	private String email;

	@Indexed(name = "phone_number", type = "string")
	private String phoneNumber;
}
