package com.artiexh.api.service.impl;

import com.artiexh.api.exception.RestException;
import com.artiexh.api.service.ProductService;
import com.artiexh.data.jpa.entity.*;
import com.artiexh.data.jpa.repository.*;
import com.artiexh.model.common.model.PageResponse;
import com.artiexh.model.domain.Merch;
import com.artiexh.model.domain.MerchStatus;
import com.artiexh.model.mapper.MerchAttachMapper;
import com.artiexh.model.mapper.MerchMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.money.Monetary;
import javax.money.UnknownCurrencyException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {
	private  final MerchMapper merchMapper;
	private final MerchAttachMapper merchAttachMapper;
	private final MerchAttachRepository attachRepository;
	private final ProductRepository productRepository;
	private final MerchCategoryRepository categoryRepository;
	private final MerchTagRepository tagRepository;
	private final ArtistRepository artistRepository;
	@Override
	public Merch getDetail(long id) {
		MerchEntity merch = productRepository.findById(id).orElseThrow(
			RestException::new
		);
		return merchMapper.entityToDomainModel(merch);
	}

	@Override
	public PageResponse<Merch> getInPage(Specification<MerchEntity> specification, Pageable pageable) {
		Page<MerchEntity> products = productRepository.findAll(specification, pageable);
		Page<Merch> productPage = products.map(entity -> merchMapper.entityToDomainModel(entity));
		PageResponse<Merch> productPageResponse = new PageResponse<>(productPage);

		return productPageResponse;
	}

	@Override
	public List<Merch> getInList(Specification<MerchEntity> specification) {
		List<MerchEntity> products = productRepository.findAll(specification);
		List<Merch> productListResponse = merchMapper.entitiesToDomainModels(products);
		return productListResponse;
	}

	@Override
	public Merch create(Merch productModel) {
		try {
			Monetary.getCurrency(productModel.getCurrency());
		} catch (UnknownCurrencyException e) {
			throw new RestException();
		}

		MerchEntity product = merchMapper.domainModelToEntity(productModel);

		Set<MerchTagEntity> tagEntities = productModel.getTags().stream()
			.map(tagName -> MerchTagEntity.builder()
				.name(tagName)
				.build())
			.collect(Collectors.toSet());
		Set<MerchTagEntity> savedTagEntities = tagRepository.findAllByNameIn(new ArrayList<>(productModel.getTags()));
		tagEntities.removeAll(savedTagEntities);
		savedTagEntities.addAll(tagRepository.saveAll(tagEntities));

		Set<MerchCategoryEntity> categoryEntities = categoryRepository.findAllByNameIn(new ArrayList<>(productModel.getCategories()));

		Set<MerchAttachEntity> attachEntities = merchAttachMapper.domainModelsToEntities(productModel.getAttaches());
		List<MerchAttachEntity> attachEntitiesList = attachRepository.saveAll(attachEntities);

		ArtistEntity artist = artistRepository.findById(productModel.getOwnerId()).orElseThrow(
			RestException::new
		);

		product.setOwner(artist);
		product.setTags(savedTagEntities);
		product.setCategories(categoryEntities);
		product.setAttaches(new HashSet<>(attachEntitiesList));
		product = productRepository.save(product);

		Merch productResponse = merchMapper.entityToDomainModel(product);
		return productResponse;
	}

	@Override

	public Merch update(Merch productModel) {
		try {
			Monetary.getCurrency(productModel.getCurrency());
		} catch (UnknownCurrencyException e) {
			throw new RestException();
		}

		MerchEntity product = productRepository.findById(productModel.getId()).orElseThrow(
			RestException::new
		);

		product = merchMapper.domainModelToEntity(productModel, product);

		Set<MerchTagEntity> tagEntities = productModel.getTags().stream()
			.map(tagName -> MerchTagEntity.builder()
				.name(tagName)
				.build())
			.collect(Collectors.toSet());
		Set<MerchTagEntity> savedTagEntities = tagRepository.findAllByNameIn(new ArrayList<>(productModel.getTags()));
		tagEntities.removeAll(savedTagEntities);
		savedTagEntities.addAll(tagRepository.saveAll(tagEntities));

		Set<MerchCategoryEntity> categoryEntities = categoryRepository.findAllByNameIn(new ArrayList<>(productModel.getCategories()));

		Set<MerchAttachEntity> attachEntities = merchAttachMapper.domainModelsToEntities(productModel.getAttaches());
		List<MerchAttachEntity> attachEntitiesList = attachRepository.saveAll(attachEntities);

		product.setTags(savedTagEntities);
		product.setCategories(categoryEntities);
		product.setAttaches(new HashSet<>(attachEntitiesList));
		product = productRepository.save(product);

		Merch productResponse = merchMapper.entityToDomainModel(product);
		return productResponse;
	}

	@Override
	public void delete(long id) {
		productRepository.delete(id, (byte) MerchStatus.NOT_AVAILABLE.getValue());
	}
}
