package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ArtistEntity;
import com.artiexh.model.domain.Artist;
import com.artiexh.model.rest.account.AccountProfile;
import com.artiexh.model.rest.artist.response.ArtistProfileResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {AccountMapper.class,
		ProductMapper.class,
		ProvinceMapper.class,
		ProductCategoryMapper.class,
		ProductTagMapper.class,
		ProductAttachMapper.class,
		ShopMapper.class,
		PasswordMapper.class,
		AddressMapper.class
	}
)
public abstract class ArtistMapper {

	@Named("basicArtistInfo")
	@Mapping(target = "subscriptionsTo", ignore = true)
	@Mapping(target = "shoppingCart", ignore = true)
	@Mapping(target = "products", ignore = true)
	@Mapping(target = "subscriptionsFrom", ignore = true)
	public abstract Artist basicArtistInfo(ArtistEntity artist);

	public abstract AccountProfile entityToAccountProfile(ArtistEntity userEntity);

	@Mapping(target = "shoppingCart", ignore = true)
	@Mapping(target = "products", ignore = true)
	public abstract Artist entityToDomain(ArtistEntity userEntity);

	abstract ArtistEntity domainToEntity(Artist user);

	@Named("idToDomain")
	public abstract Artist idToDomain(Long id);

	@Mapping(target = "numOfSubscriptions", ignore = true)
	@Mapping(target = "subscriptionsFrom", ignore = true)
	public abstract ArtistProfileResponse entityToProfileResponse(ArtistEntity entity);
}
