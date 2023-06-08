package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ArtistEntity;
import com.artiexh.model.domain.Artist;
import com.artiexh.model.domain.ArtistInfo;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {UserStatusMapper.class, RoleMapper.class, MerchMapper.class}
)
public interface ArtistMapper {

	Artist entityToDomain(ArtistEntity userEntity);

	ArtistEntity domainToEntity(Artist user);

	ArtistInfo entityToInfo(ArtistEntity entity);

}
