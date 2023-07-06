package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProductCategoryEntity;
import com.artiexh.model.domain.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductCategoryMapper {
	ProductCategory entityToDomain(ProductCategoryEntity productCategoryEntity);

	default ProductCategoryEntity domainToEntity(Long categoryId) {
		return ProductCategoryEntity.builder().id(categoryId).build();
	}

}
