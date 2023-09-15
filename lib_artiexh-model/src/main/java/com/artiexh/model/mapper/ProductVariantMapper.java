package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProductVariantCombinationEntity;
import com.artiexh.data.jpa.entity.ProductVariantEntity;
import com.artiexh.model.domain.ProductVariant;
import com.artiexh.model.domain.ProvidedProductType;
import com.artiexh.model.domain.VariantCombination;
import com.artiexh.model.rest.providedproduct.ProductVariantDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {ProductBaseMapper.class}
)
public interface ProductVariantMapper {
	ProductVariant entityToDomain(ProductVariantEntity entity);

	@Mapping(target = "productBase", ignore = true)
	ProductVariant detailToDomain(ProductVariantDetail detail);

	@Mapping(target = "productBase", source = "productBase", qualifiedByName = "domainToInfo")
	ProductVariantDetail domainToDetail(ProductVariant domain);
	ProductVariantEntity domainToEntity(ProductVariant domain);

	@Mapping(source = "id.variantId", target = "variantId")
	@Mapping(source = "id.optionValueId", target = "optionValueId")
	VariantCombination entityToDomain(ProductVariantCombinationEntity entity);

	@Mapping(target = "id.variantId", source = "variantId")
	@Mapping(target = "id.optionValueId", source = "optionValueId")
	ProductVariantCombinationEntity domainToEntity(VariantCombination domain);

	default Integer toValue(ProvidedProductType type) {
		return type.getValue();
	}

	default ProvidedProductType toProvidedProductType(Integer value) {
		return ProvidedProductType.fromValue(value);
	}
}
