package com.example.demo.service;

import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.example.demo.data.struc.User;
import com.example.demo.data.struc.UserDao;
import com.example.demo.data.validation.UserValidator;

/*
 * The class connects to a data-source.
 * Its a good idea to validate here as well.
 */
@Service
@Validated // mandatory for validation framework to kick in for @Service layer
public class UserService {

	@Autowired
	private UserDao userRepo;

	private UserValidator validator;

	public UserService(UserValidator userValidator) {
		this.validator = userValidator;
	}

	public User findById(long id) {
		Optional<User> foundUser = userRepo.findById(id);
		return foundUser.isPresent() ? foundUser.get() : null; // Ideally EntityNotFoundException
	}

	// TODO: Not working ; Also try a custom object with @Constraint annotation if its not an entity
	public User findByLastname(@Valid @Size(min = 4) String name) {
		System.out.println(name);
		return userRepo.findUserByLastnameLikeNative(name);
	}

	public void updateUser(User user) {
		// @Valid - goes for @Entity in case of @DataJpaTest
		// If we want custom we rather use validator.validate...()
		validator.validateInput(user);
		userRepo.save(user);
	}

	public void sayHello(UserDTO users){
        users.getUsers().forEach(validator::validate);
        log.info("Hello {} greeted", users.getCrowdId());
	}
}
