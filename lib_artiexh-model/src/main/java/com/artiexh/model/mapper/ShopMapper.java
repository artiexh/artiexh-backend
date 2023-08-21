package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ShopEntity;
import com.artiexh.model.domain.Shop;
import com.artiexh.model.rest.artist.request.RegistrationShopRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ShopMapper {
	ShopEntity domainToEntity(Shop shop);

	@Mapping(target = "name", source = "shopName")
	Shop requestToDomain(RegistrationShopRequest request);

	@Mapping(target = "isDefault", source = "default")
	Shop entityToDomain(ShopEntity entity);
}
