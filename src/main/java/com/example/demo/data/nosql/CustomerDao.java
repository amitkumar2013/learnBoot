package com.example.demo.data.nosql;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerDao extends MongoRepository<Customer, String> {

    @Query("{ 'firstName' : ?0 }")
	public Customer findByFirstName(String firstName);

	public List<Customer> findByLastName(String lastName);

}
