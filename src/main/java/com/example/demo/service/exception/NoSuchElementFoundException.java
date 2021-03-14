package com.example.demo.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Sample exception mapped to an error code.
 * 
 * @author amit.30.kumar
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NoSuchElementFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NoSuchElementFoundException() {
		super();
	}

	public NoSuchElementFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NoSuchElementFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSuchElementFoundException(String message) {
		super(message);
	}

	public NoSuchElementFoundException(Throwable cause) {
		super(cause);
	}

}
