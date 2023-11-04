package com.artiexh.api.base.service;

public interface SystemConfigService {

	String get(String key);

	String getOrDefault(String key, String defaultValue);

	void set(String key, String value);

	void set(String key, String value, String updateBy);

	void reload();

}
