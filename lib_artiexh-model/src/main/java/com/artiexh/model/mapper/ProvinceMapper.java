package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProvinceEntity;
import com.artiexh.model.domain.Province;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProvinceMapper {

	Province entityToDomain(ProvinceEntity provinceEntity, @Context CycleAvoidingMappingContext context);

}
