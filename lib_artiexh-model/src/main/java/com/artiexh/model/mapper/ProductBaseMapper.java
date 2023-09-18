package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProductBaseEntity;
import com.artiexh.model.domain.Model3DCode;
import com.artiexh.model.domain.ProductBase;
import com.artiexh.model.rest.productbase.ProductBaseDetail;
import com.artiexh.model.rest.productbase.ProductBaseInfo;
import org.mapstruct.*;

import java.util.Set;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {ProviderMapper.class}
)
public interface ProductBaseMapper {
	ProductBase entityToDomain(ProductBaseEntity entity, @Context CycleAvoidingMappingContext context);

	ProductBaseEntity domainToEntity(ProductBase domain);

	ProductBase detailToDomain(ProductBaseDetail detail);

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
}
