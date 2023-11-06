package com.artiexh.api.base.service;

import java.util.function.Supplier;

public interface SystemConfigService {

	String get(String key);

	String getOrDefault(String key, String defaultValue);

	<X extends Throwable> String getOrThrow(String key, Supplier<? extends X> exceptionSupplier) throws X;

	void set(String key, String value);

	void set(String key, String value, String updateBy);

	void reload();

}
