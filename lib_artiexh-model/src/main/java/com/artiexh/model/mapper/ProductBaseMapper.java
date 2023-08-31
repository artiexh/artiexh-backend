package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProductBaseEntity;
import com.artiexh.model.domain.Model3DCode;
import com.artiexh.model.domain.ProductBase;
import com.artiexh.model.domain.ProductStatus;
import com.artiexh.model.rest.productbase.ProductBaseDetail;
import com.artiexh.model.rest.productbase.ProductBaseInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProductBaseMapper {
	@Mapping(target = "price.unit", source = "priceUnit")
	@Mapping(target = "price.amount", source = "priceAmount")
	ProductBase entityToDomain(ProductBaseEntity entity);

	@Mapping(target = "priceUnit", source = "price.unit")
	@Mapping(target = "priceAmount", source = "price.amount")
	ProductBaseEntity domainToEntity(ProductBase domain);

	ProductBase detailToDomain(ProductBaseDetail detail);

	ProductBaseDetail domainToDetail(ProductBase domain);

	@Named("domainToInfo")
	ProductBaseInfo domainToInfo(ProductBase domain);

	default Integer toValue(Model3DCode code) {
		return code.getValue();
	}

	default Model3DCode toModel3DCode(Integer value) {
		return Model3DCode.fromValue(value);
	}
}
