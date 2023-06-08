package com.artiexh.api.base.response;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
		var responseException = new ResponseStatusException(HttpStatus.NO_CONTENT, ex.getMessage(), ex);
		return handleExceptionInternal(responseException, null, new HttpHeaders(), HttpStatus.NO_CONTENT, request);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
		var responseException = new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		return handleExceptionInternal(responseException, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	public ResponseEntity<Object> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex, WebRequest request) {
		var responseException = new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		return handleExceptionInternal(responseException, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		var errors = ex.getBindingResult().getFieldErrors().stream()
			.map(FieldError::getDefaultMessage)
			.collect(Collectors.joining(";"));
		var responseException = new ResponseStatusException(HttpStatus.BAD_REQUEST, errors, ex);
		return handleExceptionInternal(responseException, null, headers, HttpStatus.BAD_REQUEST, request);
	}
}
