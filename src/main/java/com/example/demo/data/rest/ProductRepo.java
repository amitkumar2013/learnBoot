package com.example.demo.data.rest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.data.rest.model.Product;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
}
