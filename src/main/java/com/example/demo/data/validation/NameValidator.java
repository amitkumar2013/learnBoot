package com.example.demo.data.validation;

import java.util.Optional;

import com.example.demo.data.struc.model.User;

public class NameValidator implements UserValidator {

	@Override
	public void validate(User user) {
	    Optional.ofNullable(user)
        .map(User::getFirstName)
        .map(String::trim)
        .filter(firstName -> !firstName.isEmpty())
        .orElseThrow(() -> new IllegalArgumentException("First Name should be provided"));
	}

	@Override
	public void validateInput(User user) {
		validate(user);
	}

}
