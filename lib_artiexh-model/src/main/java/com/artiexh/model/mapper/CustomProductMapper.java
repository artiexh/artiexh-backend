package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProductInCampaignEntity;
import com.artiexh.data.jpa.entity.ProductInCampaignTagEntity;
import com.artiexh.data.jpa.entity.ProductVariantCombinationEntity;
import com.artiexh.model.domain.ProductInCampaign;
import com.artiexh.model.rest.campaign.request.ProductInCampaignRequest;
import com.artiexh.model.rest.campaign.response.InventoryItemResponse;
import com.artiexh.model.rest.campaign.response.ProductInCampaignResponse;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.*;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {ProductAttachMapper.class, InventoryMapper.class, ArtistMapper.class, CampaignMapper.class, DateTimeMapper.class})
public interface CustomProductMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "campaign", ignore = true)
	@Mapping(target = "priceUnit", source = "price.unit")
	@Mapping(target = "priceAmount", source = "price.amount")
	@Mapping(target = "customProduct.id", source = "customProductId")
	@Mapping(target = "tags", ignore = true)
	ProductInCampaignEntity createRequestToEntity(ProductInCampaignRequest request);

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "campaign", ignore = true)
	@Mapping(target = "priceUnit", source = "price.unit")
	@Mapping(target = "priceAmount", source = "price.amount")
	@Mapping(target = "customProduct.id", source = "customProductId")
	@Mapping(target = "tags", ignore = true)
	void createRequestToEntity(ProductInCampaignRequest request, @MappingTarget ProductInCampaignEntity entity);

	default String customProductTagEntityToName(ProductInCampaignTagEntity tagEntity) {
		if (tagEntity == null) {
			return null;
		}

		return tagEntity.getName();
	}

	default ProductInCampaignTagEntity tagNameToTagEntity(String tagName) {
		ProductInCampaignTagEntity tagEntity = new ProductInCampaignTagEntity();
		if (StringUtils.isNotBlank(tagName)) {
			tagEntity.setName(tagName);
		}

		return tagEntity;
	}

	@Named("entityToResponse")
	@Mapping(target = "price.amount", source = "priceAmount")
	@Mapping(target = "price.unit", source = "priceUnit")
	@Mapping(target = "customProduct.productBase", source = "customProduct.variant.productBase")
	@Mapping(target = "customProduct.variant.variantCombination", source = "customProduct.variant.variantCombinations")
	@Mapping(target = "providerConfig", ignore = true)
	ProductInCampaignResponse entityToResponse(ProductInCampaignEntity product);

	@IterableMapping(qualifiedByName = "entityToResponse")
	Set<ProductInCampaignResponse> entityToResponse(Set<ProductInCampaignEntity> product);

	default Set<InventoryItemResponse.VariantCombinationResponse> variantCombinationEntitiesToKeyValue(Set<ProductVariantCombinationEntity> variantCombinations) {
		return variantCombinations.stream()
			.map(entity -> new InventoryItemResponse.VariantCombinationResponse(
				entity.getOptionValue().getOption().getName(),
				entity.getOptionValue().getName(),
				entity.getOptionValue().getValue()
			))
			.collect(Collectors.toSet());
	}


	@Named("entityToDomain")
	@Mapping(target = "price.amount", source = "priceAmount")
	@Mapping(target = "price.unit", source = "priceUnit")
	@Mapping(target = "customProduct", qualifiedByName = "entityToDomainWithoutVariant")
	ProductInCampaign entityToDomain(ProductInCampaignEntity product);

	@Named("domainToEntity")
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "modifiedDate", ignore = true)
	@Mapping(target = "priceUnit", source = "price.unit")
	@Mapping(target = "priceAmount", source = "price.amount")
	ProductInCampaignEntity domainToEntity(ProductInCampaign productInCampaign);
}
