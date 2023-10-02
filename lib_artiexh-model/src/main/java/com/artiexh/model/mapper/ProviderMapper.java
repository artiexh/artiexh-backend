package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProductVariantProviderEntity;
import com.artiexh.data.jpa.entity.ProviderEntity;
import com.artiexh.model.domain.Provider;
import com.artiexh.model.rest.provider.ProviderConfigResponse;
import com.artiexh.model.rest.provider.ProviderDetail;
import com.artiexh.model.rest.provider.ProviderInfo;
import org.mapstruct.*;

import java.util.Set;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {ProductVariantMapper.class, ProductBaseMapper.class, ProviderCategoryMapper.class}
)
public interface ProviderMapper {
	//Provider entityToDomain(ProviderEntity entity);

	@Mapping(target = "categories", source = "categoryIds")
	Provider detailToDomain(ProviderDetail detail);

	ProviderEntity domainToEntity(Provider domain);

	ProviderEntity domainToEntity(Provider domain, @MappingTarget ProviderEntity entity);

	Provider entityToDomain(ProviderEntity entity, @Context CycleAvoidingMappingContext context);

	@Mapping(target = "productBases", source = "productBases", qualifiedByName = "domainSetToInfoSet")
	ProviderDetail domainToDetail(Provider domain);

	@Mapping(target = "productBases", source = "productBases", ignore = true)
	@Named("domainToDetailWithoutProductBases")
	ProviderDetail domainToDetailWithoutProductBases(Provider domain);

	@IterableMapping(qualifiedByName = "domainToDetailWithoutProductBases")
	@Named("domainSetToDetailSetWithoutProductBases")
	Set<ProviderDetail> domainSetToDetailSetWithoutProductBases(Set<Provider> domain);

	@Named("domainToInfo")
	ProviderInfo domainToInfo(Provider domain);

	default Provider businessCodeToDomain(String businessCode) {
		return Provider.builder().businessCode(businessCode).build();
	}

	@Mapping(target = "variantId", source = "id.productVariantId")
	ProviderConfigResponse productVariantProviderEntityToProviderConfigResponse(ProductVariantProviderEntity entity);

}
