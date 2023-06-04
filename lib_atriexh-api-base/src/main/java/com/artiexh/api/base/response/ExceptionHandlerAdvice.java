package com.artiexh.api.base.response;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.Instant;

@ControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> createResponseEntity(Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
		ResponseModel responseModel;

		String path = null;
		if (request instanceof ServletWebRequest servletWebRequest) {
			path = servletWebRequest.getRequest().getRequestURI();
		}

		if (body instanceof ProblemDetail problemDetail) {
			responseModel = new ResponseModel(
				Instant.now(),
				statusCode.value(),
				((HttpStatus) statusCode).name(),
				problemDetail.getDetail(),
				path,
				null
			);
		} else {
			responseModel = new ResponseModel(
				Instant.now(),
				statusCode.value(),
				((HttpStatus) statusCode).name(),
				body.toString(),
				path,
				null
			);
		}

		return new ResponseEntity<>(responseModel, headers, statusCode);
	}

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
}
