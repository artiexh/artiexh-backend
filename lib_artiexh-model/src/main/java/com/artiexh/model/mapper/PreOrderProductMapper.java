package com.artiexh.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {ProductCategoryMapper.class, ProductTagMapper.class, AccountMapper.class, ProductAttachMapper.class, ProductMapper.class}
)
public interface PreOrderProductMapper {

//	PreOrderProduct entityToDomain(PreOrderProductEntity preOrderProductEntity);
//
//	PreOrderProductEntity domainToEntity(PreOrderProduct preOrderProduct);

}
