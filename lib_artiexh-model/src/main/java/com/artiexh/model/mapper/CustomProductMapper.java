package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.CustomProductEntity;
import com.artiexh.data.jpa.entity.CustomProductTagEntity;
import com.artiexh.data.jpa.entity.ProductVariantCombinationEntity;
import com.artiexh.model.rest.campaign.request.CustomProductRequest;
import com.artiexh.model.rest.campaign.response.CustomProductResponse;
import com.artiexh.model.rest.campaign.response.InventoryItemResponse;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

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
	@Mapping(target = "inventoryItem.productBase", source = "inventoryItem.variant.productBase")
	@Mapping(target = "inventoryItem.variant.variantCombination", source = "inventoryItem.variant.variantCombinations")
	@Mapping(target = "providerConfig", ignore = true)
	CustomProductResponse entityToResponse(CustomProductEntity product);

	@IterableMapping(qualifiedByName = "entityToResponse")
	Set<CustomProductResponse> entityToResponse(Set<CustomProductEntity> product);

	default Set<InventoryItemResponse.VariantCombinationResponse> variantCombinationEntitiesToKeyValue(Set<ProductVariantCombinationEntity> variantCombinations) {
		return variantCombinations.stream()
			.map(entity -> new InventoryItemResponse.VariantCombinationResponse(
				entity.getOptionValue().getOption().getName(),
				entity.getOptionValue().getName(),
				entity.getOptionValue().getValue()
			))
			.collect(Collectors.toSet());
	}

}
