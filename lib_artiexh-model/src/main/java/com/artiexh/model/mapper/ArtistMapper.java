package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ArtistEntity;
import com.artiexh.model.domain.Artist;
import com.artiexh.model.rest.account.AccountProfile;
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
		PasswordMapper.class}
)
public abstract class ArtistMapper {

	@Named("basicArtistInfo")
	@Mapping(target = "subscriptionsTo", ignore = true)
	@Mapping(target = "shoppingCart", ignore = true)
	@Mapping(target = "products", ignore = true)
	@Mapping(target = "subscriptionsFrom", ignore = true)
	@Mapping(target = "province", qualifiedByName = "provinceEntityToDomain")
	public abstract Artist basicArtistInfo(ArtistEntity artist);

	public abstract AccountProfile entityToAccountProfile(ArtistEntity userEntity);

	@Mapping(target = "province", qualifiedByName = "provinceEntityToDomain")
	@Mapping(target = "shoppingCart", ignore = true)
	public abstract Artist entityToDomain(ArtistEntity userEntity);

	abstract ArtistEntity domainToEntity(Artist user);

}
