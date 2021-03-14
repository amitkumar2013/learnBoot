package com.example.demo.data.unstruc;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

// Optional `/product` is default instead override it as `/prod`
@RepositoryRestResource(collectionResourceRel = "prod", path = "prod")
public interface ProductDao extends JpaRepository<Product, Long> {
	
}

// PagingAndSortingRepository will yield http://localhost:8080/prod{?page,size,sort}

// @NoRepositoryBean - in case of common parent bean