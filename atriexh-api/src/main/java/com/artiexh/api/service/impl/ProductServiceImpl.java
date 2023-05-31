package com.artiexh.api.service.impl;

import com.artiexh.api.exception.RestException;
import com.artiexh.api.service.ProductService;
import com.artiexh.data.jpa.entity.MerchEntity;
import com.artiexh.data.jpa.repository.ProductRepository;
import com.artiexh.model.common.model.PageResponse;
import com.artiexh.model.domain.Merch;
import com.artiexh.model.mapper.MerchMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
	private  final MerchMapper mapper;
	private final ProductRepository productRepository;
	@Override
	public Merch getDetail(long id) {
		MerchEntity merch = productRepository.findById(id).orElseThrow(
			RestException::new
		);
		return mapper.entityToDomainModel(merch);
	}

	@Override
	public PageResponse<Merch> getInPage(Specification<MerchEntity> specification, Pageable pageable) {
		Page<MerchEntity> products = productRepository.findAll(specification, pageable);
		PageResponse<Merch> productPageResponse = new PageResponse<>(products);
		return productPageResponse;
	}

	@Override
	public List<Merch> getInList(Specification<MerchEntity> specification) {
		List<MerchEntity> products = productRepository.findAll(specification);
		List<Merch> productListResponse = mapper.entitiesToDomainModels(products);
		return productListResponse;
	}

	@Override
	public Merch create(Merch productModel) {
		MerchEntity product = mapper.domainModelToEntity(productModel);
		product = productRepository.save(product);
		Merch productResponse = mapper.entityToDomainModel(product);
		return productResponse;
	}

	@Override
	public Merch update(Merch productModel) {
		MerchEntity product = productRepository.findById(productModel.getId()).orElseThrow(
			RestException::new
		);
		product = mapper.domainModelToEntity(productModel, product);
		product = productRepository.save(product);
		Merch productResponse = mapper.entityToDomainModel(product);
		return productResponse;
	}

	@Override
	public void delete(long id) {
		productRepository.delete(id);
	}
}
