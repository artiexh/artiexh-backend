package com.artiexh.api.service;

import com.artiexh.model.rest.address.ProvinceResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AddressService {
	Page<ProvinceResponse> getInPage(Pageable pageable);
}
