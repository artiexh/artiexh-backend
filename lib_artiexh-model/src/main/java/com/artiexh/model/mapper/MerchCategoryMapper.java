package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.MerchCategoryEntity;
import com.artiexh.model.domain.MerchCategory;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MerchCategoryMapper {
	MerchCategory entityToDomain(MerchCategoryEntity merchCategoryEntity);

	default MerchCategoryEntity domainToEntity(Long categoryId) {
		return MerchCategoryEntity.builder().id(categoryId).build();
	}
}
