package com.example.demo.data.validation;

import com.example.demo.data.struc.User;

public interface UserValidator {
	  void validate(User user);
	  void validateInput(User user);
}
