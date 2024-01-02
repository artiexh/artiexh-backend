package com.artiexh.api.service.impl;

import com.artiexh.api.service.AddressService;
import com.artiexh.model.domain.Country;
import com.artiexh.model.domain.District;
import com.artiexh.model.domain.Province;
import com.artiexh.model.domain.Ward;
import com.artiexh.model.rest.address.ProvinceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service("redisAddressService")
@RequiredArgsConstructor
public class RedisAddressServiceImpl implements AddressService {
	private final StringRedisTemplate stringRedisTemplate;

	@Override
	public Page<ProvinceResponse> getInPage(Pageable pageable) {
		// TODO get data from redis
		return null;
	}

	@Override
	public Page<Country> getCountries(Pageable pageable) {
		// TODO get data from redis
		return null;
	}

	@Override
	public Page<Province> getProvinces(Short countryId, Pageable pageable) {
		// TODO get data from redis
		return null;
	}

	@Override
	public Page<District> getDistricts(Short provinceId, Pageable pageable) {
		// TODO get data from redis
		return null;
	}

	@Override
	public Page<Ward> getWards(Short districtId, Pageable pageable) {
		// TODO get data from redis
		return null;
	}
}
