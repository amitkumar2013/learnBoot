package com.example.demo.api;

import javax.annotation.security.RolesAllowed;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

/**
 * Class shows method based Access Control.
 */
@RestController
@Slf4j
public class ServiceController {

	@RolesAllowed("user")
	@GetMapping("/test")
	String greet() {
		log.info("In Greet API");
		return "Hello";
	}

	@RolesAllowed("admin")
	@GetMapping("/admin")
	String admin() {
		log.info("In Admin API");
		return "Power User can only be here!";
	}

	@RolesAllowed({ "user", "admin" })
	@GetMapping("/home")
	String home() {
		log.info("In Home API");
		return "Nice to be here!";
	}

	@GetMapping("/contactus")
	String contactus() {
		log.info("In Contact us API");
		return "Open page!";
	}
}
