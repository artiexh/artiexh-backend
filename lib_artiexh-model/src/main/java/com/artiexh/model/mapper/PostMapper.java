package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.PostEntity;
import com.artiexh.model.domain.Post;
import com.artiexh.model.rest.post.PostDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {AccountMapper.class, ArtistMapper.class, DateTimeMapper.class, ProductAttachMapper.class}
)
public interface PostMapper {
	@Mapping(target = "owner", source = "owner", qualifiedByName = "basicArtistInfo")
	Post entityToDomain(PostEntity entity);

	@Mapping(target = "createdDate", qualifiedByName = "fromUTCToLocal")
	@Mapping(target = "modifiedDate", qualifiedByName = "fromUTCToLocal")
	PostDetail domainToDetail(Post post);

	Post detailToDomain(PostDetail postDetail);

	PostEntity domainToEntity(Post post);
}
