package com.example.demo.data.graphql;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.demo.commons.PetAnimal;
import com.example.demo.data.graphql.model.Pet;

import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.extern.slf4j.Slf4j;

/*
 * TODO - Not working - can't query.
 */
@Component
@Slf4j
public class PetQuery implements GraphQLQueryResolver {

	public List<Pet> pets() {		
		List<Pet> pets = new ArrayList<>();

		Pet aPet = new Pet();
		aPet.setId(1l);
		aPet.setName("Bill");
		aPet.setAge(9);
		aPet.setType(PetAnimal.MAMMOTH);

		pets.add(aPet);
		log.info("Providing Pets!");
		return pets;
	}
}
