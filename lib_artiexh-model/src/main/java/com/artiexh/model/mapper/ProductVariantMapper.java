package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProductVariantCombinationEntity;
import com.artiexh.data.jpa.entity.ProductVariantEntity;
import com.artiexh.data.jpa.entity.ProductVariantProviderEntity;
import com.artiexh.model.domain.ProductVariant;
import com.artiexh.model.domain.ProductVariantProvider;
import com.artiexh.model.domain.ProvidedProductType;
import com.artiexh.model.domain.VariantCombination;
import com.artiexh.model.rest.productvariant.ProductVariantDetail;
import com.artiexh.model.rest.productvariant.request.UpdateProductVariantDetail;
import org.mapstruct.*;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {ProductBaseMapper.class, ProviderMapper.class}
)
public interface ProductVariantMapper {
	ProductVariant entityToDomain(ProductVariantEntity entity, @Context CycleAvoidingMappingContext context);

	@Mapping(target = "productBase", ignore = true)
	ProductVariant detailToDomain(ProductVariantDetail detail);

	ProductVariant updateRequestToDomain(UpdateProductVariantDetail detail);

	@Mapping(target = "productBase", source = "productBase", qualifiedByName = "domainToInfo")
	ProductVariantDetail domainToDetail(ProductVariant domain);

	ProductVariantEntity domainToEntity(ProductVariant domain);

	@Mapping(target = "productBase", ignore = true)
	@Mapping(target = "productBaseId", ignore = true)
	ProductVariantEntity domainToEntity(ProductVariant domain, @MappingTarget ProductVariantEntity entity);

	@Mapping(source = "id.variantId", target = "variantId")
	@Mapping(source = "id.optionValueId", target = "optionValueId")
	VariantCombination entityToDomain(ProductVariantCombinationEntity entity);

	@Mapping(target = "id.variantId", source = "variantId")
	@Mapping(target = "id.optionValueId", source = "optionValueId")
	ProductVariantCombinationEntity domainToEntity(VariantCombination domain);

	@Mapping(source = "id.productVariantId", target = "variantId")
	@Mapping(source = "id.businessCode", target = "businessCode")
	ProductVariantProvider entityToDomain(ProductVariantProviderEntity productVariantProviderEntity, @Context CycleAvoidingMappingContext context);

	@Mapping(target = "id.productVariantId", source = "variantId")
	@Mapping(target = "id.businessCode", source = "businessCode")
	ProductVariantProviderEntity domainToEntity(ProductVariantProvider entity);

	default Integer toValue(ProvidedProductType type) {
		return type.getValue();
	}

	default ProvidedProductType toProvidedProductType(Integer value) {
		return ProvidedProductType.fromValue(value);
	}
}
