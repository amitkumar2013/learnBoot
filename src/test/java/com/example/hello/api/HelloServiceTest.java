package hello.api;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import hello.api.person.Person;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HelloServiceTest {

	@Autowired
	private HelloService helloService;

	// ---- generic test
	@Test
	public void test() {
		PersonDTO persons = createPersonList(createPerson());
		helloService.sayHello(persons);
	}

	// ---- validation specific test
	@Test
	public void sayHello_id_Invalid_error() {
		Person person = createPerson();
		person.setId(null);
		PersonDTO persons = createPersonList(person);
		assertThatIllegalArgumentException().isThrownBy(() -> helloService.sayHello(persons));
	}

	// ---- validation specific test
	@Test
	public void sayHello_age_Invalid_error() {
		Person person = createPerson();
		person.setAge("string");
		PersonDTO persons = createPersonList(person);

		assertThatIllegalArgumentException().isThrownBy(() -> helloService.sayHello(persons))
				.withCauseInstanceOf(NumberFormatException.class);
	}

	// Util methods
	private PersonDTO createPersonList(Person person) {
		PersonDTO personDTO = new PersonDTO();
		personDTO.setCrowdId("class crowd");
		personDTO.setPersons(Collections.singletonList(person));
		return personDTO;
	}

	private Person createPerson() {
		Person person = new Person();
		person.setId(1L);
		person.setFirstName("Amit");
		person.setLastName("Kumar");
		person.setAge("11");
		return person;
	}

}
