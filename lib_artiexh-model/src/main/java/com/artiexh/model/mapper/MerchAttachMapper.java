package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.MerchAttachEntity;
import com.artiexh.model.domain.MerchAttach;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Set;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {MerchAttachTypeMapper.class}
)
public interface MerchAttachMapper {

	MerchAttach entityToDomain(MerchAttachEntity merchAttachEntity);

	MerchAttachEntity domainToEntity(MerchAttach merchAttach);

	Set<MerchAttachEntity> domainModelsToEntities(Set<MerchAttach> models);

	Set<MerchAttachEntity> domainModelsToEntities(Set<MerchAttach> models, @MappingTarget Set<MerchAttachEntity> entities);
}
