package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.MerchEntity;
import com.artiexh.model.domain.Merch;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {UserStatusMapper.class, RoleMapper.class, MerchStatusMapper.class, MerchTypeMapper.class, DeliveryTypeMapper.class, MerchCategoryMapper.class, MerchTagMapper.class, MerchAttachTypeMapper.class}
)
public interface MerchMapper {

	Merch entityToDomain(MerchEntity merchEntity);

	MerchEntity domainToEntity(Merch merch);

}
