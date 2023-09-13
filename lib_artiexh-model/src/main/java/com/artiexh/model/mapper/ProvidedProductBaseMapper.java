package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProductVariantCombinationEntity;
import com.artiexh.data.jpa.entity.ProvidedProductBaseEntity;
import com.artiexh.model.domain.ProvidedProductBase;
import com.artiexh.model.domain.ProvidedProductType;
import com.artiexh.model.domain.VariantCombination;
import com.artiexh.model.rest.providedproduct.ProvidedProductBaseDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {ProductBaseMapper.class}
)
public interface ProvidedProductBaseMapper {
	ProvidedProductBase entityToDomain(ProvidedProductBaseEntity entity);

	@Mapping(source = "businessCode", target = "providedProductBaseId.businessCode")
	@Mapping(source = "productBaseId", target = "providedProductBaseId.productBaseId")
	@Mapping(target = "productBase", ignore = true)
	ProvidedProductBase detailToDomain(ProvidedProductBaseDetail detail);

	@Mapping(target = "businessCode", source = "providedProductBaseId.businessCode")
	@Mapping(target = "productBaseId", source = "providedProductBaseId.productBaseId")
	@Mapping(target = "productBase", source = "productBase", qualifiedByName = "domainToInfo")
	ProvidedProductBaseDetail domainToDetail(ProvidedProductBase domain);
	ProvidedProductBaseEntity domainToEntity(ProvidedProductBase domain);

	@Mapping(source = "id.variantId", target = "variantId")
	@Mapping(source = "id.optionId", target = "optionId")
	@Mapping(source = "id.optionValueId", target = "optionValueId")
	VariantCombination entityToDomain(ProductVariantCombinationEntity entity);

	@Mapping(target = "id.variantId", source = "variantId")
	@Mapping(target = "id.optionId", source = "optionId")
	@Mapping(target = "id.optionValueId", source = "optionValueId")
	ProductVariantCombinationEntity domainToEntity(VariantCombination domain);

	default Integer toValue(ProvidedProductType type) {
		return type.getValue();
	}

	default ProvidedProductType toProvidedProductType(Integer value) {
		return ProvidedProductType.fromValue(value);
	}
}
