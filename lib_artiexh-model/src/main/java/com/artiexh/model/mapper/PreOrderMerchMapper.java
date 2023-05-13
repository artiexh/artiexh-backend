package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.PreOrderMerchEntity;
import com.artiexh.model.domain.PreOrderMerch;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {UserStatusMapper.class, RoleMapper.class, MerchStatusMapper.class, MerchTypeMapper.class, DeliveryTypeMapper.class, MerchCategoryMapper.class, MerchTagMapper.class, MerchAttachTypeMapper.class}
)
public interface PreOrderMerchMapper {

    PreOrderMerch entityToDomain(PreOrderMerchEntity preOrderMerchEntity);

    PreOrderMerchEntity domainToEntity(PreOrderMerch preOrderMerch);

}
