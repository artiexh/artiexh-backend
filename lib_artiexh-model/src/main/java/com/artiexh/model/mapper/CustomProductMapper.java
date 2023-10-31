package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.CustomProductEntity;
import com.artiexh.data.jpa.entity.CustomProductTagEntity;
import com.artiexh.model.domain.CustomProduct;
import com.artiexh.model.rest.customproduct.CustomProductDetail;
import org.mapstruct.*;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {MediaMapper.class, ArtistMapper.class, ProductVariantMapper.class, DateTimeMapper.class}
)
public interface CustomProductMapper {
	@Mapping(target = "artist", source = "artistId", qualifiedByName = "idToDomain")
	@Mapping(target = "variant", source = "variantId", qualifiedByName = "idToDomain")
	@Mapping(target = "thumbnail", source = "thumbnailId", qualifiedByName = "idToDomain")
	@Mapping(target = "category.id", source = "categoryId")
	CustomProduct detailToDomain(CustomProductDetail detail);

	@Mapping(target = "variant", source = "variant", qualifiedByName = "domainToDetail")
	CustomProductDetail domainToDetail(CustomProduct item);

	@Mapping(target = "tags", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "modifiedDate", ignore = true)
	CustomProductEntity domainToEntity(CustomProduct item, @MappingTarget CustomProductEntity entity);

	@Mapping(target = "tags", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "modifiedDate", ignore = true)
	CustomProductEntity domainToEntity(CustomProduct item);

	CustomProduct entityToDomain(CustomProductEntity entity, @Context CycleAvoidingMappingContext context);

	@Mapping(target = "variant", qualifiedByName = "entityToBasicDomain")
	CustomProduct entityToDomain(CustomProductEntity entity);

	@Named("entityToDomainWithoutVariant")
	@Mapping(target = "variant", ignore = true)
	@Mapping(target = "artist", qualifiedByName = "basicArtistInfo")
	CustomProduct entityToDomainWithoutVariant(CustomProductEntity entity);

	default String customProductTagToTag(CustomProductTagEntity tag) {
		if (tag == null) {
			return null;
		}

		return tag.getName();
	}

}
