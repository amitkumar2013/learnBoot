package com.example.demo.data.validation;

import java.util.Optional;

import com.example.demo.data.struc.User;
import com.example.demo.data.struc.UserDao;

public class EntityValidator implements UserValidator {

	private UserDao userRepo;

	public EntityValidator(UserDao userRepo) {
		this.userRepo = userRepo;
	}

	@Override
	public void validate(User user) {
		Long personId = Optional.ofNullable(user.getId()).filter(id -> (id > 0))
				.orElseThrow(() -> new IllegalArgumentException("A person must be specified."));

		if (userRepo.findById(personId) != null) {
			throw new IllegalArgumentException("Given person [" + personId + "] does not exist.");
		}
	}

	@Override
	public void validateInput(User user) {
		Optional.ofNullable(user.getId()).filter(id -> (id > 0))
				.orElseThrow(() -> new IllegalArgumentException("A person must be specified."));		
	}

}
