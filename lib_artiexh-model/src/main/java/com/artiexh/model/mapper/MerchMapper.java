package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.MerchEntity;
import com.artiexh.model.domain.Merch;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {UserStatusMapper.class, RoleMapper.class, MerchStatusMapper.class, MerchTypeMapper.class, DeliveryTypeMapper.class, MerchCategoryMapper.class, MerchTagMapper.class, MerchAttachTypeMapper.class}
)
public interface MerchMapper {

	Merch entityToDomainModel(MerchEntity merchEntity);

	List<Merch> entitiesToDomainModels(List<MerchEntity> merchEntity);
	MerchEntity domainModelToEntity(Merch merch);

	MerchEntity domainModelToEntity(Merch merch, @MappingTarget MerchEntity entity);

}
