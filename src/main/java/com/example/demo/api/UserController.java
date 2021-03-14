package com.example.demo.api;

import java.util.HashMap;
import java.util.Map;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import javax.validation.constraints.Digits;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.data.struc.User;
import com.example.demo.service.GreetingService;
import com.example.demo.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/")
@Validated // mandatory for validation framework to kick in for @PathVariable & @RequestParam
@CrossOrigin(origins = "http://localhost:4200") // Only to allow angular hosted on a separate port.
@Slf4j
public class UserController {
    
	@Autowired
	private GreetingService apiService;
	@Autowired
	private UserService userService;

    @GetMapping
    public String index() {
    	log.debug("Greeting service invoked");
        return "Greetings from Spring Boot!";
    }
    @PostMapping
    public void sayHello(@Valid @RequestBody UserDTO users) {
        userService.sayHello(person);
    }
    
    @GetMapping(path = "/hello/{name}")
    public String hello(@PathVariable String name) {
    	log.debug("hello service invoked with path variable");
        return apiService.greet(name);
    }

    // @PathVariable as well as @RequestParam - can be validated in-line else use @Valid and refer class annotations.
    // Don't forget class level @Validated and handling ConstraintViolationException
    @GetMapping(path = "/user/id/{id}")
    public String retrieveByPathVariable(@PathVariable @Digits(integer = 2, fraction = 0) int id) {
    	log.debug("user service invoked with path variable to fetch from db with validation at ctrl layer");
        return userService.findById(id).getName();
    }

	@GetMapping(path = "/user")
    public String retrieveByRequestParam(@RequestParam @Digits(integer = 2, fraction = 0) int id) {
    	log.debug("user service invoked with request param to fetch from db with validation at ctrl layer");
    	User searchedUser = userService.findById(id);
        return (searchedUser!=null) ? searchedUser.getName(): null;
    }

    @GetMapping(path = "/user/name/{name}")
    public String retrieveByName(@PathVariable String name) {
    	log.debug("user service invoked with path variable to fetch from db with validation at svc layer");
        return userService.findByLastname(name).getName();
    }
    
    // API is 1st level of validation, this does not discounts the 2nd level of service validations.
    // Its also a good practice to build validation framework for the 2nd level of service validations.
    @PostMapping("/updateUser")
	public void updateUser(@Valid @RequestBody User user) {
    	log.debug("user service invoked with request body i.e. POST with validation at ctrl layer");
		userService.updateUser(user);
		log.debug("User Updated " + user);
	}
    
    // Spring throws this exception for all @Valid failures
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getAllErrors().forEach((error) -> {
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
			});
		return errors;
    }
    
    // For all valid failures for path variables and request parameters.
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ConstraintViolationException.class)
	ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
		return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
	}
}
