package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProvidedProductBaseEntity;
import com.artiexh.model.domain.ProvidedProductBase;
import com.artiexh.model.rest.provider.ProvidedProductBaseDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProvidedProductBaseMapper {
	ProvidedProductBase entityToDomain(ProvidedProductBaseEntity entity);

	@Mapping(source = "businessCode", target = "id.businessCode")
	@Mapping(source = "productBaseId", target = "id.productBaseId")
	@Mapping(source = "price.unit", target = "priceUnit")
	@Mapping(source = "price.amount", target = "priceAmount")
	ProvidedProductBase detailToDomain(ProvidedProductBaseDetail detail);

	@Mapping(target = "businessCode", source = "id.businessCode")
	@Mapping(target = "productBaseId", source = "id.productBaseId")
	@Mapping(target = "price.unit", source = "priceUnit")
	@Mapping(target = "price.amount", source = "priceAmount")
	ProvidedProductBaseDetail domainToDetail(ProvidedProductBase domain);
	ProvidedProductBaseEntity domainToEntity(ProvidedProductBase domain);
}
