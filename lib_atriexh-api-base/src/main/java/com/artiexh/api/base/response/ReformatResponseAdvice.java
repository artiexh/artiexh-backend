package com.artiexh.api.base.response;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ReformatResponseAdvice implements ResponseBodyAdvice<Object> {

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return !returnType.getContainingClass().getPackage().getName().contains("org.springdoc");
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
								  Class<? extends HttpMessageConverter<?>> selectedConverterType,
								  ServerHttpRequest request, ServerHttpResponse response) {
		Instant now = Instant.now();
		int status = ((ServletServerHttpResponse) response).getServletResponse().getStatus();
		HttpStatus httpStatus = HttpStatus.resolve(status);
		Assert.notNull(httpStatus, "Invalid http status");
		String path = request.getURI().getPath();

		if (body instanceof ProblemDetail problemDetail) {
			return new ResponseModel(now, status, httpStatus.name(), problemDetail.getDetail(), path, null);
		} else {
			return new ResponseModel(now, status, httpStatus.name(), null, path, body);
		}
	}

}
