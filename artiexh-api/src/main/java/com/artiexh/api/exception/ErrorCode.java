package com.artiexh.api.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
	//Product
	PRODUCT_EXISTED(HttpStatus.BAD_REQUEST, "Product is existed with Id: "),
	PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "Product is not found"),
	PRODUCT_CURRENCY_INVALID(HttpStatus.BAD_REQUEST, "Product currency is invalid"),

	//Attachment
	ATTACHMENT_NOT_FOUND(HttpStatus.OK, "Attachment is not found"),

	//Category
	CATEGORY_NOT_FOUND(HttpStatus.OK, "Category is not found"),

	//User
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User is not found"),

	//Artist
	ARTIST_NOT_FOUND(HttpStatus.OK, "Artist is not found"),
	ARTIST_NOT_VALID(HttpStatus.BAD_REQUEST, "Artist is not valid"),

	//Cart
	INVALID_ITEM(HttpStatus.BAD_REQUEST, "Can not add your own product into cart"),

	//Order
	ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "Order is not found"),
	ORDER_IS_INVALID(HttpStatus.BAD_REQUEST, "Your can not get this order"),
	ORDER_STATUS_NOT_ALLOWED(HttpStatus.FORBIDDEN, "Only Admin or Owner can cancel order"),

	//Provider
	PROVIDER_NOT_FOUND(HttpStatus.NOT_FOUND, "Provider is not found"),
	PROVIDER_INVALID(HttpStatus.BAD_REQUEST, "Some providers are not allowed to access or not found or duplicated"),
	PROVIDER_EXISTED(HttpStatus.BAD_REQUEST, "Provider is existed with Business Code: "),
	PROVIDED_PRODUCT_INVALID(HttpStatus.BAD_REQUEST, "Provided product is invalid with Id: "),

	//Option - Option Value
	REQUIRED_OPTION_NOT_FOUND(HttpStatus.BAD_REQUEST, "Required option is not found for this product "),
	OPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "Option is not found with Id: "),
	OPTION_VALUE_INVALID(HttpStatus.BAD_REQUEST, "Option value is not matched with Option Id: "),

	//Variant
	VARIANT_NOT_FOUND(HttpStatus.NOT_FOUND, "Variant is not found "),

	//Media
	UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Upload file is failed!"),
	DOWNLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Download file is failed!"),
	MEDIA_NOT_FOUND(HttpStatus.BAD_REQUEST, "Media file is not found"),
	DOWNLOAD_NOT_ALLOWED(HttpStatus.FORBIDDEN, "You are not allowed to download this file"),
	OWNER_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "Shared User can not be owner"),
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
