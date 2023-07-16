package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProvidedProductEntity;
import com.artiexh.data.jpa.entity.ProviderEntity;
import com.artiexh.model.rest.providedproduct.ProvidedProductDetail;
import com.artiexh.model.rest.providedproduct.ProvidedProductInfo;
import com.artiexh.model.rest.provider.ProviderDetail;
import com.artiexh.model.rest.provider.ProviderInfo;
import org.mapstruct.*;

import java.util.Set;

@Mapper()
public interface ProvidedProductMapper {
	@Named(value = "detailToEntity")
	@Mapping(target = "id.baseModelId", source = "baseModelId")
	@Mapping(target = "id.businessCode", source = "businessCode")
	ProvidedProductEntity detailToEntity(ProvidedProductDetail detail);


	@Mapping(target = "id.baseModelId", source = "baseModelId")
	@Mapping(target = "id.businessCode", source = "businessCode")
	ProvidedProductEntity detailToEntity(ProvidedProductDetail detail, @MappingTarget ProvidedProductEntity entity);

	@Named(value = "entityToInfo")
	@Mapping(source = "id.baseModelId", target = "baseModelId")
	@Mapping(source = "id.businessCode", target = "businessCode")
	ProvidedProductInfo entityToInfo(ProvidedProductEntity entity);

	@IterableMapping(qualifiedByName = "entityToInfo")
	Set<ProvidedProductInfo> entitiesToInfos(Set<ProvidedProductEntity> entities);

	@IterableMapping(qualifiedByName = "detailToEntity")
	Set<ProvidedProductEntity> detailsToEntities(Set<ProvidedProductDetail> entities);


	ProvidedProductDetail entityToDetail(ProvidedProductEntity entity);
}
