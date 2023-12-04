package com.artiexh.api.base.response;

import com.artiexh.api.base.exception.ErrorCode;
import com.artiexh.api.base.exception.InvalidException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.Instant;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandlerAdvice extends ResponseEntityExceptionHandler {
	@ExceptionHandler({InvalidException.class})
	public ResponseEntity<?> handleBusinessException(InvalidException ex, WebRequest request) {
		var responseException = new ResponseStatusException(ex.getErrorCode().getCode(), ex.getMessage(), ex);
		ResponseModel responseModel = new ResponseModel(
			Instant.now(),
			ex.getErrorCode().name(),
			ex.getErrorCode().getCode().value(),
			ex.getErrorCode().getCode().name(),
			StringUtils.isBlank(ex.getMessage()) ? ex.getErrorCode().getMessage() : ex.getMessage(),
			null,
			null
		);
		return handleExceptionInternal(responseException, responseModel, new HttpHeaders(), ex.getErrorCode().getCode(), request);
	}


	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
		var responseException = new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		ResponseModel responseModel = new ResponseModel(
			Instant.now(),
			ErrorCode.ENTITY_NOT_FOUND.name(),
			HttpStatus.BAD_REQUEST.value(),
			HttpStatus.BAD_REQUEST.name(),
			ex.getMessage(),
			null,
			null
		);
		return handleExceptionInternal(responseException, responseModel, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
		var responseException = new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		return handleExceptionInternal(responseException, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(SQLIntegrityConstraintViolationException.class)
	public ResponseEntity<Object> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex, WebRequest request) {
		var responseException = new ResponseStatusException(HttpStatus.BAD_REQUEST, "integrity constraint violation error", ex);
		return handleExceptionInternal(responseException, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
		var responseException = new ResponseStatusException(HttpStatus.BAD_REQUEST, "data integrity", ex);
		return handleExceptionInternal(responseException, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		Pattern ENUM_MSG = Pattern.compile("\\[[a-zA-Z,_,\" ]*\\]");
		if (exception.getCause() != null && exception.getCause() instanceof InvalidFormatException) {
			Matcher match = ENUM_MSG.matcher(exception.getCause().getMessage());
			if (match.find()) {
				var responseException = new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error in processing enum: " + match.group(0), exception);
				return handleExceptionInternal(responseException, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
			}
			var responseException = new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getCause().getMessage(), exception);
			return handleExceptionInternal(responseException, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
		}

		var responseException = new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
		return handleExceptionInternal(responseException, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		var errors = ex.getBindingResult().getFieldErrors().stream()
			.map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
			.collect(Collectors.joining(";"));
		var responseException = new ResponseStatusException(HttpStatus.BAD_REQUEST, errors, ex);
		return handleExceptionInternal(responseException, null, headers, HttpStatus.BAD_REQUEST, request);
	}
}
