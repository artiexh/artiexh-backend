package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProviderCategoryEntity;
import com.artiexh.model.domain.ProviderCategory;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProviderCategoryMapper {

	ProviderCategory entityToDomain(ProviderCategoryEntity entity);

	ProviderCategoryEntity domainToEntity(ProviderCategory domain);

}
