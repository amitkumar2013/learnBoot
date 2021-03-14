package com.example.demo.service.exception;

import java.util.Objects;

import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

/**
 * Complete control of exception handling.
 * 
 * @author amit.30.kumar
 */
@Slf4j
@ControllerAdvice//(annotations = Advised.class) - only controllers with @Advised will be handled by the controller advice or basePackages
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	public static final String TRACE = "trace";

	@Value("${reflectoring.trace:false}")
	private boolean printStackTrace;

	// ------------------------------ HANDLE TYPES OF EXCEPTION -------------------------------------
	@Override // This one exists and lets override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(),
				"Validation error. Check 'errors' field for details.");
		for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
			errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
		}
		return ResponseEntity.unprocessableEntity().body(errorResponse);
	}	

    // For all valid failures for path variables and request parameters.
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(ConstraintViolationException.class)
	ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException e) {
		return new ResponseEntity<>("not valid due to validation error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
	}

	// Can also handle a list of exceptions rather than just one
	@ExceptionHandler // (ItemNotFoundException.class) - a custom exception or exception we want to handle
	@ResponseStatus(HttpStatus.NOT_FOUND)// status code we want to return. Overridden by ResponseEnity status code.
	public ResponseEntity<ErrorResponse> handleItemNotFoundException(Exception itemNotFoundException,
			WebRequest request) {
		log.error("Failed to find the requested element", itemNotFoundException);
		return buildErrorResponse(itemNotFoundException, HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler(RuntimeException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<ErrorResponse> handleAllUncaughtException(RuntimeException exception, WebRequest request) {
		log.error("Unknown error occurred", exception);
		return buildErrorResponse(exception, "Unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR, request);
	}

	// ------------------------------ RESPONSE -------------------------------------
	private ResponseEntity<ErrorResponse> buildErrorResponse(Exception exception, HttpStatus httpStatus,
			WebRequest request) {
		return buildErrorResponse(exception, exception.getMessage(), httpStatus, request);
	}

	private ResponseEntity<ErrorResponse> buildErrorResponse(Exception exception, String message, HttpStatus httpStatus,
			WebRequest request) {
		ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), exception.getMessage());
		if (printStackTrace && isTraceOn(request)) {
			errorResponse.setStackTrace(ExceptionUtils.getStackTrace(exception));
		}
		return ResponseEntity.status(httpStatus).body(errorResponse);
	}

	private boolean isTraceOn(WebRequest request) {
		String[] value = request.getParameterValues(TRACE);
		return Objects.nonNull(value) && value.length > 0 && value[0].contentEquals("true");
	}
}
