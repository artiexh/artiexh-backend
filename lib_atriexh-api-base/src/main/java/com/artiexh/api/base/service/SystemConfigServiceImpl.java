package com.artiexh.api.base.service;

import com.artiexh.data.jpa.entity.SystemConfigEntity;
import com.artiexh.data.jpa.repository.SystemConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SystemConfigServiceImpl implements SystemConfigService {

	private final ConcurrentMap<String, String> configs = new ConcurrentHashMap<>();
	private final SystemConfigRepository repository;

	@PostConstruct
	@Transactional(readOnly = true)
	public void init() {
		reload();
	}

	@Override
	public String get(String key) {
		return configs.get(key);
	}

	@Override
	public String getOrDefault(String key, String defaultValue) {
		return configs.getOrDefault(key, defaultValue);
	}

	@Override
	public <X extends Throwable> String getOrThrow(String key, Supplier<? extends X> exceptionSupplier) throws X {
		if (!configs.containsKey(key)) {
			throw exceptionSupplier.get();
		} else {
			return configs.get(key);
		}
	}

	@Override
	@Transactional
	public void set(String key, String value) {
		set(key, value, null);
	}

	@Override
	@Transactional
	public void set(String key, String value, String updateBy) {
		SystemConfigEntity entity = new SystemConfigEntity(key, value, updateBy);
		repository.save(entity);
		reload();
	}

	@Override
	@Transactional(readOnly = true)
	public void reload() {
		configs.clear();
		configs.putAll(repository.findAll().stream().collect(Collectors.toMap(
			SystemConfigEntity::getKey,
			SystemConfigEntity::getValue
		)));
	}
}
