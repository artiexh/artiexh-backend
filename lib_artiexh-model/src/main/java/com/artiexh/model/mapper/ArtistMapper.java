package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ArtistEntity;
import com.artiexh.data.jpa.entity.ProvinceEntity;
import com.artiexh.model.domain.Artist;
import com.artiexh.model.domain.ArtistInfo;
import com.artiexh.model.domain.Province;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {UserStatusMapper.class, RoleMapper.class, MerchMapper.class}
)
public interface ArtistMapper {

	@Mapping(target = "province", qualifiedByName = "provinceEntityToDomain")
	Artist entityToDomain(ArtistEntity userEntity);

	ArtistEntity domainToEntity(Artist user);

	ArtistInfo entityToInfo(ArtistEntity entity);

	@Named("provinceEntityToDomain")
	@Mapping(target = "country.provinces", ignore = true)
	Province provinceEntityToDomain(ProvinceEntity provinceEntity);

}
