package com.artiexh.api.service.impl;

import com.artiexh.api.service.AddressService;
import com.artiexh.data.jpa.repository.ProvinceRepository;
import com.artiexh.model.mapper.ProvinceMapper;
import com.artiexh.model.rest.address.ProvinceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
	private final ProvinceRepository provinceRepository;
	private final ProvinceMapper provinceMapper;

	@Override
	public Page<ProvinceResponse> getInPage(Pageable pageable) {
		return provinceRepository.findAll(pageable).map(provinceMapper::entityToResponse);
	}
	
}
