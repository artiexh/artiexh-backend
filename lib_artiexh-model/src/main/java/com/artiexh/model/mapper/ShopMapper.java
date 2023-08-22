package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ArtistEntity;
import com.artiexh.model.domain.Shop;
import com.artiexh.model.rest.artist.request.RegistrationShopRequest;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ShopMapper {
	ArtistEntity domainToEntity(Shop shop);

	Shop requestToDomain(RegistrationShopRequest request);

	Shop entityToDomain(ArtistEntity entity);
}
