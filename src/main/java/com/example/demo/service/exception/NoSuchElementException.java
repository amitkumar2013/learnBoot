package com.example.demo.service.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * A custom header than the usual.
 * Similar to class level annotation @ResponseStatus(value = HttpStatus.NOT_FOUND)
 * But with little more control.
 * 
 * @author amit.30.kumar
 */
public class NoSuchElementException extends ResponseStatusException {

	private static final long serialVersionUID = 1L;

	public NoSuchElementException(String message) {
		super(HttpStatus.NOT_FOUND, message);
	}

	@Override
	public HttpHeaders getResponseHeaders() {
		return HttpHeaders.EMPTY;
	}
}
