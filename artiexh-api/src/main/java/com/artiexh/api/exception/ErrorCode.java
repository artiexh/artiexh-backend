package com.artiexh.api.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
	//Product
	PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Product is not found"),
	PREORDER_NOT_FOUND_TIME(HttpStatus.BAD_REQUEST, "Pre-order product must require start time and end time"),
	PREORDER_INVALID_TIME(HttpStatus.BAD_REQUEST, "End time must be after start time"),
	PRODUCT_CURRENCY_INVALID(HttpStatus.BAD_REQUEST, "Product currency is invalid"),

	//Attachment
	ATTACHMENT_NOT_FOUND(HttpStatus.OK, "Attachment is not found"),

	//Category
	CATEGORY_NOT_FOUND(HttpStatus.OK, "Category is not found"),

	//Artist
	ARTIST_NOT_FOUND(HttpStatus.OK, "Artist is not found"),
	ARTIST_NOT_VALID(HttpStatus.BAD_REQUEST, "Artist is not valid"),

	//Base Model
	BASE_MODEL_NOT_FOUND(HttpStatus.NOT_FOUND, "Base model is not found"),

	//Provider
	PROVIDER_NOT_FOUND(HttpStatus.NOT_FOUND, "Provider is not found"),
	//Provided Product
	PROVIDED_PRODUCT_NOT_FOUND(HttpStatus.BAD_REQUEST, "Provider is not found"),
	PROVIDED_PRODUCT_KEY_NOT_VALID(HttpStatus.BAD_REQUEST, "Provider key is in invalid"),
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
