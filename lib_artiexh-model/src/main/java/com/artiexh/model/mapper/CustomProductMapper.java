package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.CustomProductEntity;
import com.artiexh.data.jpa.entity.CustomProductTagEntity;
import com.artiexh.model.rest.campaign.request.CustomProductRequest;
import com.artiexh.model.rest.campaign.response.CustomProductResponse;
import org.mapstruct.*;

import java.util.Set;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, uses = {ProductAttachMapper.class})
public interface CustomProductMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "campaign", ignore = true)
	@Mapping(target = "priceUnit", source = "price.unit")
	@Mapping(target = "priceAmount", source = "price.amount")
	@Mapping(target = "inventoryItem.id", source = "inventoryItemId")
	@Mapping(target = "tags", ignore = true)
	CustomProductEntity createRequestToEntity(CustomProductRequest request);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "campaign", ignore = true)
	@Mapping(target = "priceUnit", source = "price.unit")
	@Mapping(target = "priceAmount", source = "price.amount")
	@Mapping(target = "inventoryItem.id", source = "inventoryItemId")
	@Mapping(target = "tags", ignore = true)
	void createRequestToEntity(CustomProductRequest request, @MappingTarget CustomProductEntity entity);

	default String customProductTagEntityToName(CustomProductTagEntity tagEntity) {
		if (tagEntity == null) {
			return null;
		}

		return tagEntity.getName();
	}

	@Named("entityToResponse")
	@Mapping(target = "price.amount", source = "priceAmount")
	@Mapping(target = "price.unit", source = "priceUnit")
	CustomProductResponse entityToResponse(CustomProductEntity product);

	@IterableMapping(qualifiedByName = "entityToResponse")
	Set<CustomProductResponse> entityToResponse(Set<CustomProductEntity> product);
}
