package com.artiexh.api.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.api.exception.ErrorCode;
import com.artiexh.api.service.CollectionService;
import com.artiexh.api.service.ProvidedProductBaseService;
import com.artiexh.model.domain.Collection;
import com.artiexh.model.mapper.CollectionMapper;
import com.artiexh.model.rest.collection.CollectionDetail;
import com.artiexh.model.rest.collection.request.CreateCollectionRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = Endpoint.Provider.ROOT)
public class CollectionController {
	private final CollectionMapper collectionMapper;
	private final CollectionService collectionService;
	@PostMapping(Endpoint.Provider.COLLECTION)
	@PreAuthorize("hasAuthority('ARTIST')")
	public CollectionDetail createCollection(
		@PathVariable("providerId") String businessCode,
		@Valid @RequestBody CreateCollectionRequest detail) {
		Collection collection = collectionMapper.createRequestToDomain(detail);

		try {
			collection = collectionService.create(detail.getProvidedProductIds(), collection);
			return collectionMapper.domainToDetail(collection);
		} catch (EntityNotFoundException exception){
			throw new ResponseStatusException(HttpStatus.NOT_FOUND,
				ErrorCode.PRODUCT_NOT_FOUND.getMessage(),
				exception);
		} catch (IllegalArgumentException exception) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
				exception.getMessage(),
				exception);
		}
	}
}
