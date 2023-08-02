package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProvidedModelEntity;
import com.artiexh.model.rest.providedproduct.ProvidedModelDetail;
import com.artiexh.model.rest.providedproduct.ProvidedModelInfo;
import org.mapstruct.*;

import java.util.Set;

@Mapper()
public interface ProvidedModelMapper {
	@Named(value = "detailToEntity")
	@Mapping(target = "id.baseModelId", source = "baseModelId")
	@Mapping(target = "id.businessCode", source = "businessCode")
	@Mapping(target = "priceAmount", source = "price.amount")
	@Mapping(target = "priceUnit", source = "price.unit")
	ProvidedModelEntity detailToEntity(ProvidedModelDetail detail);


	@Mapping(target = "id.baseModelId", source = "baseModelId")
	@Mapping(target = "id.businessCode", source = "businessCode")
	@Mapping(target = "baseModel", ignore = true)
	@Mapping(target = "provider", ignore = true)
	@Mapping(target = "priceAmount", source = "price.amount")
	@Mapping(target = "priceUnit", source = "price.unit")
	ProvidedModelEntity detailToEntity(ProvidedModelDetail detail, @MappingTarget ProvidedModelEntity entity);

	@Named(value = "entityToInfo")
	@Mapping(source = "id.baseModelId", target = "baseModelId")
	@Mapping(source = "id.businessCode", target = "businessCode")
	@Mapping(target = "price.unit", source = "priceUnit")
	@Mapping(target = "price.amount", source = "priceAmount")
	ProvidedModelInfo entityToInfo(ProvidedModelEntity entity);

	@IterableMapping(qualifiedByName = "entityToInfo")
	Set<ProvidedModelInfo> entitiesToInfos(Set<ProvidedModelEntity> entities);

	@IterableMapping(qualifiedByName = "detailToEntity")
	Set<ProvidedModelEntity> detailsToEntities(Set<ProvidedModelDetail> entities);

	ProvidedModelDetail entityToDetail(ProvidedModelEntity entity);
}
