package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.MerchEntity;
import com.artiexh.model.domain.Merch;
import com.artiexh.model.product.request.UpdateProductRequest;
import org.mapstruct.*;

import java.util.List;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {UserStatusMapper.class, RoleMapper.class, MerchStatusMapper.class, MerchTypeMapper.class, DeliveryTypeMapper.class, MerchCategoryMapper.class, MerchTagMapper.class, MerchAttachTypeMapper.class}
)
public interface MerchMapper {

	Merch entityToDomainModel(MerchEntity merchEntity);
	Merch requestToDomainModel(UpdateProductRequest requestToDomainModel);
	List<Merch> entitiesToDomainModels(List<MerchEntity> merchEntity);

	MerchEntity domainModelToEntity(Merch merch);

	@Mapping(target = "owner", ignore = true)
	MerchEntity domainModelToEntity(Merch merch, @MappingTarget MerchEntity entity);

}
