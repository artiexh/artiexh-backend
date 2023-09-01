package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ArtistEntity;
import com.artiexh.model.domain.Shop;
import com.artiexh.model.rest.artist.RegistrationShopRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {ArtistMapper.class}
)
public interface ShopMapper {

	ArtistEntity domainToEntity(Shop shop);

	Shop requestToDomain(RegistrationShopRequest request);

	@Named("basicShopInfo")
	@Mapping(target = "owner", source = ".", qualifiedByName = "basicArtistInfo")
	@Mapping(target = "shopWard.district.wards", ignore = true)
	@Mapping(target = "shopWard.district.province.districts", ignore = true)
	@Mapping(target = "shopWard.district.province.country.provinces", ignore = true)
	Shop entityToDomain(ArtistEntity entity);

}
