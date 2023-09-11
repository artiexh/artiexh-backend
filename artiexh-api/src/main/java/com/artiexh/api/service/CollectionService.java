package com.artiexh.api.service;

import com.artiexh.data.jpa.entity.CollectionEntity;
import com.artiexh.data.jpa.entity.ProvidedProductBaseId;
import com.artiexh.model.domain.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Set;

public interface CollectionService {
	Collection create(Set<Long> providedProductIds, Collection collection);
}
