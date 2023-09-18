package com.artiexh.api.service.impl;

import com.artiexh.api.service.AddressService;
import com.artiexh.model.domain.Country;
import com.artiexh.model.domain.District;
import com.artiexh.model.domain.Province;
import com.artiexh.model.domain.Ward;
import com.artiexh.model.rest.address.ProvinceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
// Proxy class
public class AddressServiceImpl implements AddressService {
	@Qualifier("jpaAddressService")
	private final AddressService jpaAddressService;
	@Qualifier("redisAddressService")
	private final AddressService redisAddressService;

	@Override
	public Page<ProvinceResponse> getInPage(Pageable pageable) {
		var result = redisAddressService.getInPage(pageable);
		if (result.isEmpty()) {
			result = jpaAddressService.getInPage(pageable);
		}
		return result;
	}

	@Override
	public Page<Country> getCountries(Pageable pageable) {
		var result = redisAddressService.getCountries(pageable);
		if (result.isEmpty()) {
			result = jpaAddressService.getCountries(pageable);
		}
		return result;
	}

	@Override
	public Page<Province> getProvinces(Short countryId, Pageable pageable) {
		var result = redisAddressService.getProvinces(countryId, pageable);
		if (result.isEmpty()) {
			result = jpaAddressService.getProvinces(countryId, pageable);
		}
		return result;
	}

	@Override
	public Page<District> getDistricts(Short provinceId, Pageable pageable) {
		var result = redisAddressService.getDistricts(provinceId, pageable);
		if (result.isEmpty()) {
			result = jpaAddressService.getDistricts(provinceId, pageable);
		}
		return result;
	}

	@Override
	public Page<Ward> getWards(Short districtId, Pageable pageable) {
		var result = redisAddressService.getWards(districtId, pageable);
		if (result.isEmpty()) {
			result = jpaAddressService.getWards(districtId, pageable);
		}
		return result;
	}

}
