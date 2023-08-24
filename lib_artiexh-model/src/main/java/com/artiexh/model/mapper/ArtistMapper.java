package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ArtistEntity;
import com.artiexh.data.jpa.entity.ProvinceEntity;
import com.artiexh.model.domain.Artist;
import com.artiexh.model.domain.Province;
import com.artiexh.model.domain.Shop;
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

	@Mapping(target = "province", qualifiedByName = "provinceEntityToDomain")
	@Mapping(target = "shoppingCart", ignore = true)
	public abstract Artist entityToDomain(ArtistEntity userEntity);

	abstract ArtistEntity domainToEntity(Artist user);

	@Named("provinceEntityToDomain")
	@Mapping(target = "country.provinces", ignore = true)
	abstract Province provinceEntityToDomain(ProvinceEntity provinceEntity);

	//@Mapping(target = "owner", source = ".")
	abstract Shop artistEntityToShop(ArtistEntity artistEntity);

}
