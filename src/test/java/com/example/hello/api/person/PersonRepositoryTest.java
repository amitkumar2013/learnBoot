package com.example.hello.api.person;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.example.demo.data.struc.UserDao;
import com.example.demo.data.struc.model.User;

@DataJpaTest
//To avoid in process DB clash
@AutoConfigureTestDatabase(replace = Replace.NONE) 
public class PersonRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private UserDao repository;

	@Test
	public void findByUsernameShouldReturnUser() {
		this.entityManager.persist(User.builder().firstName("fn").lastName("ln").age(26).build());
		assertThat(repository.findById(4l).get().getFirstName()).isEqualTo("Amit");
	}
}
