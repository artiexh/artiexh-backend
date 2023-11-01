package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.CustomProductEntity;
import com.artiexh.data.jpa.entity.CustomProductTagEntity;
import com.artiexh.data.jpa.entity.ProductVariantCombinationEntity;
import com.artiexh.model.domain.CustomProduct;
import com.artiexh.model.rest.customproduct.*;
import org.mapstruct.*;

import java.util.Set;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {MediaMapper.class, ArtistMapper.class, ProductVariantMapper.class, DateTimeMapper.class, ProductAttachMapper.class}
)
public interface CustomProductMapper {
	@Mapping(target = "artist", source = "artistId", qualifiedByName = "idToDomain")
	@Mapping(target = "variant", source = "variantId", qualifiedByName = "idToDomain")
	@Mapping(target = "modelThumbnail", source = "modelThumbnailId", qualifiedByName = "idToDomain")
	CustomProduct detailToDomain(CustomProductGeneralRequest detail);

	CustomProductGeneralRequest domainToDetail(CustomProduct item);

	@Mapping(target = "tags", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "modifiedDate", ignore = true)
	CustomProductEntity domainToEntity(CustomProduct item, @MappingTarget CustomProductEntity entity);

	@Mapping(target = "tags", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "modifiedDate", ignore = true)
	CustomProductEntity domainToEntity(CustomProduct item);

	@Mapping(target = "variant.id", source = "variantId")
	@Mapping(target = "artist.id", source = "artistId")
	@Mapping(target = "tags", ignore = true)
	@Mapping(target = "modelThumbnail.id", source = "modelThumbnailId")
	CustomProductEntity generalRequestToEntity(CustomProductGeneralRequest detail);

	@Mapping(target = "variant.id", source = "variantId")
	@Mapping(target = "artist.id", source = "artistId")
	@Mapping(target = "tags", ignore = true)
	@Mapping(target = "modelThumbnail.id", source = "modelThumbnailId")
	CustomProductEntity designRequestToEntity(CustomProductDesignRequest detail);

	CustomProduct entityToDomain(CustomProductEntity entity, @Context CycleAvoidingMappingContext context);

	@Mapping(target = "variant", qualifiedByName = "entityToBasicDomain")
	CustomProduct entityToDomain(CustomProductEntity entity);

	CustomProductResponse entityToGetAllResponse(CustomProductEntity entity);

	@Named("customProductEntityToGeneralResponse")
	CustomProductGeneralResponse entityToGeneralResponse(CustomProductEntity entity);

	CustomProductDesignResponse entityToDesignResponse(CustomProductEntity entity);

	@Named("variantCombinationEntityToDomain")
	@Mapping(target = "optionValue.optionId", ignore = true)
	@Mapping(target = "option.id", source = "optionValue.option.id")
	@Mapping(target = "option.name", source = "optionValue.option.name")
	@Mapping(target = "option.index", source = "optionValue.option.index")
	CustomProductDesignResponse.ProductVariantCombination variantCombinationEntityToDomain(ProductVariantCombinationEntity entity);

	@IterableMapping(qualifiedByName = "variantCombinationEntityToDomain")
	Set<CustomProductDesignResponse.ProductVariantCombination> variantCombinationsEntityToDomains(Set<ProductVariantCombinationEntity> entities);

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
