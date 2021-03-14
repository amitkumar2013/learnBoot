package com.example.demo.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.example.demo.data.struc.UserDao;
import com.example.demo.data.validation.AgeValidator;
import com.example.demo.data.validation.EntityValidator;
import com.example.demo.data.validation.NameValidator;
import com.example.demo.data.validation.UserValidator;
import com.example.demo.data.validation.UserValidatorComposite;

/**
 * In case there are a number of Validators and we want to group them.
 * 
 * @author amit.30.kumar
 */
@Configuration
public class ValidatorConfiguration {

	@Bean
	AgeValidator ageValidator() {
		return new AgeValidator();
	}

	@Bean
	NameValidator nameValidator() {
		return new NameValidator();
	}

	@Bean
	EntityValidator entityValidator(UserDao userRepo) {
		return new EntityValidator(userRepo);
	}

	// This is autowired in UserService
	@Bean
	@Primary
	UserValidator orderItemValidator(UserDao userRepo) {
		return new UserValidatorComposite(Arrays.asList(entityValidator(userRepo), nameValidator(), ageValidator()));
	}
}
