package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProductBaseEntity;
import com.artiexh.model.domain.*;
import com.artiexh.model.rest.productbase.ProductBaseDetail;
import com.artiexh.model.rest.productbase.ProductBaseInfo;
import com.artiexh.model.rest.productbase.request.UpdateProductBaseDetail;
import com.artiexh.model.rest.productbase.request.UpdateProviderConfig;
import com.artiexh.model.rest.productvariant.ProductVariantDetail;
import org.mapstruct.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {ProviderMapper.class, ProductCategoryMapper.class, ProductAttachMapper.class, MediaMapper.class, DateTimeMapper.class}
)
public interface ProductBaseMapper {
	@Mapping(target = "createdDate", qualifiedByName = "fromUTCToLocal")
	@Mapping(target = "modifiedDate", qualifiedByName = "fromUTCToLocal")
	ProductBase entityToDomain(ProductBaseEntity entity, @Context CycleAvoidingMappingContext context);

	ProductBaseEntity domainToEntity(ProductBase domain);

	@Mapping(target = "providers", ignore = true)
	@Mapping(target = "category", ignore = true)
	ProductBaseEntity domainToEntity(ProductBase domain, @MappingTarget ProductBaseEntity entity);

	@Mapping(target = "category", source = "categoryId")
	@Mapping(target = "providers", source = "businessCodes")
	@Mapping(target = "modelFile", source = "modelFileId", qualifiedByName = "idToDomain")
	ProductBase detailToDomain(ProductBaseDetail detail);

	@Mapping(target = "category", source = "categoryId")
	@Mapping(target = "modelFile", source = "modelFileId", qualifiedByName = "idToDomain")
	ProductBase detailToDomain(UpdateProductBaseDetail detail);

	@Mapping(target = "providers", source = "providers", qualifiedByName = "domainSetToDetailSetWithoutProductBases")
	ProductBaseDetail domainToDetail(ProductBase domain);

	@Named("domainToInfo")
	ProductBaseInfo domainToInfo(ProductBase domain);

	@IterableMapping(qualifiedByName = "domainToInfo")
	@Named("domainSetToInfoSet")
	Set<ProductBaseInfo> domainSetToInfoSet(Set<ProductBase> domainSet);

	default Integer toValue(Model3DCode code) {
		return code.getValue();
	}

	default Model3DCode toModel3DCode(Integer value) {
		return Model3DCode.fromValue(value);
	}

	default ProductBase idToDomain(Long productId) {
		return ProductBase.builder().id(productId).build();
	}

	default ProductBase detailToDomain(UpdateProviderConfig detail) {
		ProductBase productBase = new ProductBase();
		productBase.setProviders(
			detail.getProviders().stream()
				.map(businessCode -> Provider.builder().businessCode(businessCode).build())
				.collect(Collectors.toSet())
		);

		Set<ProductVariant> variants = new HashSet<>();
		for (Map.Entry<String, Set<ProductVariantDetail.ProviderConfig>> entry : detail.getProviderConfigs().entrySet()) {
			Long variantId = Long.parseLong(entry.getKey());
			if (!entry.getValue().stream()
				.map(ProductVariantDetail.ProviderConfig::getBusinessCode)
				.collect(Collectors.toSet())
				.equals(detail.getProviders())) {
				throw new IllegalArgumentException("All providers must be supported in variant " + variantId);
			}
			ProductVariant variant = ProductVariant.builder()
				.id(variantId)
				.providerConfigs(
					entry.getValue().stream()
						.map(providerConfig -> ProductVariantProvider.builder()
							.variantId(variantId)
							.basePriceAmount(providerConfig.getBasePriceAmount())
							.businessCode(providerConfig.getBusinessCode())
							.manufacturingTime(providerConfig.getManufacturingTime())
							.minQuantity(providerConfig.getMinQuantity())
							.build())
						.collect(Collectors.toSet())
				)
				.build();

			variants.add(variant);
		}

		productBase.setProductVariants(variants);
		return productBase;
	}
}
