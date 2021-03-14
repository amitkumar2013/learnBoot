package com.example.demo.data.graphql.model;

import com.example.demo.commons.PetAnimal;

import lombok.Data;

@Data
public class Pet {
	private Long id;
	private String name;
	private PetAnimal type;
	private int age;
}