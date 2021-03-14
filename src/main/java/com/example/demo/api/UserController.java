package com.example.demo.api;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Digits;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.data.struc.model.User;
import com.example.demo.service.MiscService;
import com.example.demo.service.UserService;

import lombok.extern.slf4j.Slf4j;

/**
 * The rest controller is a demo for validation to request inputs.
 * 
 * @author amit.30.kumar
 */
@RestController
//@RequestMapping(value = "/") // Class level - method levels are appended to this path mapping - most confusing
@Validated // mandatory for validation framework to kick in for @PathVariable & @RequestParam
@CrossOrigin(origins = "http://localhost:8080") // Only to allow ui hosted on a different port.
@Slf4j
public class UserController {
    
	/*
	 * Extract User Name from Spring Security Context stored as AuthenticationPrincipal
	 */
	@GetMapping("/user")
    public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
        return Collections.singletonMap("name", principal.getAttribute("name"));
    }
	
	/*
	 * Extracts error message from session. Its captured by Security configuration interceptor.
	 */
	@GetMapping("/error")
	@ResponseBody
	public String error(HttpServletRequest request) {
		String message = (String) request.getSession().getAttribute("error.message");
		request.getSession().removeAttribute("error.message");
		return message;
	}

	// String for liveliness - one could very well return a view by mapping
    @GetMapping("/ping")
    public String index() {
        return "pong!";
    }
    
    // -------------------------- VALIDATION --------------------------
    
	// ### Showcase a basic (Greeting) Service - GET Validation
	@Autowired
	private MiscService apiService;
    // Showcase path variables
    @GetMapping(path = "/hello/{name}")
    public String hello(@PathVariable String name) {
    	log.debug("hello service invoked with path variable");
        return apiService.greet(name);
    }    
	// ### Showcase a basic data (User) Service - POST Validation
	@Autowired
	private UserService userService;
    // Showcase Validation with POST - using DTO based validation right here in controller, 
    // you may move it to Service layer as well.
    @PostMapping(path = "/hello")
    public void sayHello(@Valid @RequestBody UserDTO person) {
        userService.sayHello(person);
    }
    // Spring throws this exception for all @Valid failures - A better version in GlobalExceptionHandler.java
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

    // ### Validation at CONTROLLER - PATH VARIABLE as well as REQUEST PARAM
    
    // @PathVariable as well as @RequestParam - can be validated in-line else use @Valid and refer class annotations.
    // Don't forget class level @Validated and handling ConstraintViolationException
    @GetMapping(path = "/usr/id/{id}")
    public String retrieveByPathVariable(@PathVariable @Digits(integer = 2, fraction = 0) int id) {
    	log.debug("user service invoked with path variable to fetch from db with validation at ctrl layer");
        return userService.findById(id).getName();
    }
	@GetMapping(path = "/usr")
    public String retrieveByRequestParam(@RequestParam @Digits(integer = 2, fraction = 0) int id) {
    	log.debug("user service invoked with request param to fetch from db with validation at ctrl layer");
    	User searchedUser = userService.findById(id);
        return (searchedUser!=null) ? searchedUser.getName(): null;
    }

    // ### Validation at SERVICE 
    @GetMapping(path = "/usr/name/{name}")
    public String retrieveByName(@PathVariable String name) {
    	log.debug("user service invoked with path variable to fetch from db with validation at svc layer");
        return userService.findByLastname(name).getName();
    }
    
    // ### Validation at SERVICE - Custom as well as ENTITY based 
    // API is 1st level of validation, this does not discounts the 2nd level of service validations.
    // Its also a good practice to build validation framework for the 2nd level of service validations.
    @PostMapping("/updateUsr")
	public void updateUser(@Valid @RequestBody User user) {
    	log.debug("user service invoked with request body i.e. POST with validation at ctrl layer");
		userService.updateUser(user);
		log.debug("User Updated " + user);
	}
    
	// ---------------------------------------------------------------------------
    
}
