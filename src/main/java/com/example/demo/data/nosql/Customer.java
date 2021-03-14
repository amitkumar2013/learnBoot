package com.example.demo.data.nosql;

import java.io.Serializable;

import javax.persistence.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Builder;
import lombok.Data;

@Data
@Document(collection = "customer")
@Builder
@SuppressWarnings("serial")
public class Customer implements Serializable {
	
	@Id
	private String id;

	private String firstName;
	private String lastName;

}
