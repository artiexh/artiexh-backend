package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ArtistEntity;
import com.artiexh.model.domain.Shop;
import com.artiexh.model.rest.artist.RegistrationShopRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {ArtistMapper.class, AddressMapper.class})
public interface ShopMapper {

	ArtistEntity domainToEntity(Shop shop);

	Shop requestToDomain(RegistrationShopRequest request);

	@Named("basicShopInfo")
	@Mapping(target = "owner", source = ".", qualifiedByName = "basicArtistInfo")
	Shop entityToDomain(ArtistEntity entity);

}
