package com.example.hello.api;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.demo.api.UserDTO;
import com.example.demo.data.struc.model.User;
import com.example.demo.service.MiscService;

@ExtendWith(SpringExtension.class)
// @SpringBootTest is an overkill - it will get the whole context
public class HelloServiceTest {

	@Autowired
	private MiscService helloService;

	// ---- generic test
	@Test
	public void test() {
		UserDTO persons = createPersonList(createPerson());
		helloService.greet(persons.getUsers().get(0).getLastName());
	}

	// ---- validation specific test
	@Test
	public void sayHello_id_Invalid_error() {
		User person = createPerson();
		UserDTO persons = createPersonList(person);
		assertThatIllegalArgumentException().isThrownBy(() -> helloService.greet(persons.getUsers().get(0).getLastName()));
	}

	// ---- validation specific test
	@Test
	public void sayHello_age_Invalid_error() {
		User person = createPerson();
		person.setAge(-11);
		UserDTO persons = createPersonList(person);

		assertThatIllegalArgumentException().isThrownBy(() -> helloService.greet(persons.getUsers().get(0).getLastName()))
				.withCauseInstanceOf(NumberFormatException.class);
	}

	// Util methods
	private UserDTO createPersonList(User person) {
		UserDTO personDTO = new UserDTO();
		personDTO.setCrowdId("class crowd");
		personDTO.setUsers(Collections.singletonList(person));
		return personDTO;
	}

	private User createPerson() {
		return User.builder().firstName("Amit").lastName("Kumar").age(11).build();
	}

}
