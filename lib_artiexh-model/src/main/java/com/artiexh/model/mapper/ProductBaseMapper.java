package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProductBaseEntity;
import com.artiexh.model.domain.ProductBase;
import com.artiexh.model.rest.productbase.ProductBaseDetail;
import com.artiexh.model.rest.productbase.ProductBaseInfo;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductBaseMapper {
	ProductBase entityToDomain(ProductBaseEntity entity);

	ProductBaseEntity domainToEntity(ProductBase domain);

	ProductBase detailToDomain(ProductBaseDetail detail);

	ProductBaseDetail domainToDetail(ProductBase domain);

	ProductBaseInfo domainToInfo(ProductBase domain);
}
