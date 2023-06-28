package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProductTagEntity;
import com.artiexh.model.domain.ProductTag;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductTagMapper {

	default String entityToDomain(ProductTagEntity productTagEntity) {
		return productTagEntity.getName();
	}

	default ProductTagEntity domainToEntity(String productTag) {
		return ProductTagEntity.builder().name(productTag).build();
	}

	default Set<String> tagsDomainToTagsResponse(Set<ProductTag> tags) {
		if (tags == null) {
			return Collections.emptySet();
		}
		return tags.stream().map(ProductTag::getName).collect(Collectors.toSet());
	}

	default Set<ProductTag> tagsRequestToTagsDomain(Set<String> tags) {
		return tags.stream().map(tag -> {
			ProductTag productTag = new ProductTag();
			productTag.setName(tag);
			return productTag;
		}).collect(Collectors.toSet());
	}

}
