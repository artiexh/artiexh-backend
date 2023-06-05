package com.artiexh.api.base.response;

import java.time.Instant;

public record ResponseModel(
	Instant timestamp,
	int status,
	String name,
	String message,
	String path,
	Object data
) {
}
