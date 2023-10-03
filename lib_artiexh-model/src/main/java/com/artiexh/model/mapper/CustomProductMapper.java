package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.CustomProductEntity;
import com.artiexh.data.jpa.entity.CustomProductTagEntity;
import com.artiexh.model.rest.campaign.request.CreateCustomProductRequest;
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
	@Mapping(target = "category.id", source = "productCategoryId")
	@Mapping(target = "tags", ignore = true)
	CustomProductEntity createRequestToEntity(CreateCustomProductRequest request);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "campaign", ignore = true)
	@Mapping(target = "priceUnit", source = "price.unit")
	@Mapping(target = "priceAmount", source = "price.amount")
	@Mapping(target = "inventoryItem.id", source = "inventoryItemId")
	@Mapping(target = "category.id", source = "productCategoryId")
	@Mapping(target = "tags", ignore = true)
	void createRequestToEntity(CreateCustomProductRequest request, @MappingTarget CustomProductEntity entity);

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
