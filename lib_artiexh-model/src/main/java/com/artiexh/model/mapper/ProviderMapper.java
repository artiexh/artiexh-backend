package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProviderEntity;
import com.artiexh.model.domain.Provider;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {ProvidedProductBaseMapper.class}
)
public interface ProviderMapper {
	Provider entityToDomain(ProviderEntity entity);
}
