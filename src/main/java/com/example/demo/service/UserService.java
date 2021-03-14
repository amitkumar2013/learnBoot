package com.example.demo.service;

import java.util.Locale;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.example.demo.api.UserDTO;
import com.example.demo.commons.I18Constants;
import com.example.demo.data.struc.UserDao;
import com.example.demo.data.struc.model.User;
import com.example.demo.data.validation.UserValidator;
import com.example.demo.service.exception.NoSuchElementFoundException;

import lombok.extern.slf4j.Slf4j;

/**
 * Basic Service class connects to a data-source. Its a good idea to validate
 * here as well.
 * 
 * @author amit.30.kumar
 */
@Service
@Validated // mandatory for validation framework to kick in for @Service layer
@Slf4j
public class UserService {

	@Autowired
	private UserDao userRepo;
	private MessageSource messageSource;

	// Besides @Autowired; Other way to wire - setters
	private UserValidator validator;

	public UserService(UserValidator userValidator) {
		this.validator = userValidator;
	}

	public User findById(long id) throws NoSuchElementFoundException {
		boolean vanilla = false;
		if (vanilla) {
			Optional<User> foundUser = userRepo.findById(id);
			return foundUser.isPresent() ? foundUser.get() : null; // Ideally EntityNotFoundException
		} else {
			return userRepo.findById(id).orElseThrow(() -> new NoSuchElementFoundException(
					getLocalMessage(I18Constants.NO_ITEM_FOUND.getKey(), String.valueOf(id))));
		}
	}

	private String getLocalMessage(String key, String... params) {
		return messageSource.getMessage(key, params, Locale.ENGLISH);
	}

	// TODO: Not working ; Also try a custom object with @Constraint annotation if
	// its not an entity
	public User findByLastname(@Valid @Size(min = 4) String name) {
		log.debug(name);
		return userRepo.findUserByLastNameLikeNative(name);
	}

	// WRAPPED MANUAL VALIDATION USING CONFIGURED VALIDATOR
	public void updateUser(User user) {
		// @Valid - goes for @Entity in case of @DataJpaTest
		// If we want custom we rather use validator.validate...()
		validator.validateInput(user);
		userRepo.save(user);
	}

	// MANUAL VALIDATION
	public void sayHello(UserDTO users) {
		users.getUsers().forEach(validator::validate);
		log.info("Hello {} greeted", users.getCrowdId());
	}
}
