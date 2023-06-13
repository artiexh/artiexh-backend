package com.artiexh.api.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
	//Product
	PRODUCT_NOT_FOUND(HttpStatus.OK, "Product is not found"),
	PRODUCT_CURRENCY_INVALID(HttpStatus.BAD_REQUEST, "Product currency is invalid"),

	//Category
	CATEGORY_NOT_FOUND(HttpStatus.OK, "Category is not found"),

	//Artist
	ARTIST_NOT_FOUND(HttpStatus.OK, "Artist is not found"),

	//AUTH
	ACCOUNT_INFO_NOT_FOUND(HttpStatus.OK, "Can not get account information from request");
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
