package hello.api.validation;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import hello.api.person.Person;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AgeValidatorTest {

	@Autowired
	EntityValidator entityValidator;

	@Test
	public void validate_ageNull_invalid() {
		Person person = new Person();
		AgeValidator validator = new AgeValidator();

		assertThatIllegalArgumentException().isThrownBy(() -> validator.validate(person));
	}

	@Test
	public void validate_person_valid() {
		Person person = new Person(-1L, "fn", "ln", "26");
		assertThatIllegalArgumentException().isThrownBy(() -> entityValidator.validate(person));
	}
}
