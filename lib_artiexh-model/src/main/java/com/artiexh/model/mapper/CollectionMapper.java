package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.CollectionEntity;
import com.artiexh.model.domain.Collection;
import com.artiexh.model.rest.collection.CollectionDetail;
import com.artiexh.model.rest.collection.request.CreateCollectionRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {ProvidedProductBaseMapper.class}
)
public interface CollectionMapper {
	CollectionDetail domainToDetail(Collection collection);
	@Mapping(target = "providedProducts", ignore = true)
	Collection createRequestToDomain(CreateCollectionRequest request);
	Collection detailToDomain(CollectionDetail collection);
	Collection entityToDomain(CollectionEntity collection);
}
