package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.MerchCategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MerchCategoryMapper {

	default String entityToDomain(MerchCategoryEntity merchCategoryEntity) {
		return merchCategoryEntity.getName();
	}

	default MerchCategoryEntity domainToEntity(String merchCategory) {
		return MerchCategoryEntity.builder().name(merchCategory).build();
	}
}
