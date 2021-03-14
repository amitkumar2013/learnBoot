package com.example.hello.api.validation;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.example.demo.data.struc.model.User;
import com.example.demo.data.validation.AgeValidator;
import com.example.demo.data.validation.EntityValidator;

@ExtendWith(SpringExtension.class)
public class AgeValidatorTest {

	@Autowired
	EntityValidator entityValidator;

	@Test
	public void validate_ageNull_invalid() {
		User person = User.builder().build();
		AgeValidator validator = new AgeValidator();

		assertThatIllegalArgumentException().isThrownBy(() -> validator.validate(person));
	}

	@Test
	public void validate_person_valid() {
		User person = User.builder().id(-1l).firstName("fn").lastName("ln").age(26).build();
		assertThatIllegalArgumentException().isThrownBy(() -> entityValidator.validate(person));
	}
}
