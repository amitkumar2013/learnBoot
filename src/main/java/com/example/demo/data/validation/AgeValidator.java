package com.example.demo.data.validation;

import java.math.BigDecimal;
import java.util.Optional;

import com.example.demo.data.struc.model.User;

public class AgeValidator implements UserValidator {

	@Override
	public void validate(User user) {
		Integer yearOld = Optional.ofNullable(user)
				.map(User::getAge)
		        .filter(age -> age > 18)
		        .orElseThrow(() -> new IllegalArgumentException("Age cannot be less than 18."));

		    try {
		      new BigDecimal(yearOld);
		    } catch (NumberFormatException ex) {
		      throw new IllegalArgumentException("Given age [" + yearOld + "] is not in valid format", ex);
		    }
	}

	@Override
	public void validateInput(User user) {
		validate(user);
	}

}
