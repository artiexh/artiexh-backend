package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.MerchAttachEntity;
import com.artiexh.model.domain.MerchAttach;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {MerchAttachTypeMapper.class}
)
public interface MerchAttachMapper {

	MerchAttach entityToDomain(MerchAttachEntity merchAttachEntity);

	MerchAttachEntity domainToEntity(MerchAttach merchAttach);
}
