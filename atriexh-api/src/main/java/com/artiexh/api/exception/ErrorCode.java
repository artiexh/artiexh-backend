package com.artiexh.api.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
	//Product
	PRODUCT_NOT_FOUND(HttpStatus.BAD_REQUEST, "Product not found"),
	PRODUCT_CURRENCY_INVALID(HttpStatus.BAD_REQUEST, "Product currency is invalid")
	,

	//Artist
	ARTIST_NOT_FOUND(HttpStatus.BAD_REQUEST, "Artist not found");
	private final HttpStatus statusCode;
	private final String message;

	ErrorCode(HttpStatus statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}

	public HttpStatus getCode() {
		return statusCode;
	}

	public String getMessage() {
		return message;
	}

}
