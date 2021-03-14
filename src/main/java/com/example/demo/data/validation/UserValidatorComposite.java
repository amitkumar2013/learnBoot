package com.example.demo.data.validation;

import java.util.List;

import com.example.demo.data.struc.model.User;

/**
 * These classes are registered in the spring context by ValidatorConfiguration using new
 * Could have been @Component - which they are not by definition. 
 * 
 * @author amit.30.kumar
 */
public class UserValidatorComposite implements UserValidator {

	private final List<UserValidator> validators;

	public UserValidatorComposite(List<UserValidator> validators) {
		this.validators = validators;
	}

	@Override
	public void validate(User user) {
		validators.forEach(validators -> validators.validate(user));
	}

	@Override
	public void validateInput(User user) {
		validators.get(1).validate(user);
		validators.get(2).validate(user);
	}
	
	

}