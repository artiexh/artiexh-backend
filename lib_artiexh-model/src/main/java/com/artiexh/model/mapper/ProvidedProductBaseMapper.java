package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProvidedProductBaseEntity;
import com.artiexh.model.domain.PaymentMethod;
import com.artiexh.model.domain.ProvidedProductBase;
import com.artiexh.model.domain.ProvidedProductType;
import com.artiexh.model.rest.provider.ProvidedProductBaseDetail;
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
	@Mapping(source = "price.unit", target = "priceUnit")
	@Mapping(source = "price.amount", target = "priceAmount")
	@Mapping(target = "productBase", ignore = true)
	ProvidedProductBase detailToDomain(ProvidedProductBaseDetail detail);

	@Mapping(target = "businessCode", source = "providedProductBaseId.businessCode")
	@Mapping(target = "productBaseId", source = "providedProductBaseId.productBaseId")
	@Mapping(target = "price.unit", source = "priceUnit")
	@Mapping(target = "price.amount", source = "priceAmount")
	@Mapping(target = "productBase", source = "productBase", qualifiedByName = "domainToInfo")
	ProvidedProductBaseDetail domainToDetail(ProvidedProductBase domain);
	ProvidedProductBaseEntity domainToEntity(ProvidedProductBase domain);

	default Integer toValue(ProvidedProductType type) {
		return type.getValue();
	}

	default ProvidedProductType toProvidedProductType(Integer value) {
		return ProvidedProductType.fromValue(value);
	}
}
