package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProvidedProductBaseEntity;
import com.artiexh.model.domain.ProvidedProductBase;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProvidedProductBaseMapper {
	ProvidedProductBase entityToDomain(ProvidedProductBaseEntity entity);
}
