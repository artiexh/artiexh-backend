package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.InventoryItemEntity;
import com.artiexh.data.jpa.entity.InventoryItemTagEntity;
import com.artiexh.model.domain.InventoryItem;
import com.artiexh.model.rest.inventory.InventoryItemDetail;
import org.mapstruct.*;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {MediaMapper.class, ArtistMapper.class, ProductVariantMapper.class, DateTimeMapper.class}
)
public interface InventoryMapper {
	@Mapping(target = "artist", source = "artistId", qualifiedByName = "idToDomain")
	@Mapping(target = "variant", source = "variantId", qualifiedByName = "idToDomain")
	@Mapping(target = "thumbnail", source = "thumbnailId", qualifiedByName = "idToDomain")
	InventoryItem detailToDomain(InventoryItemDetail detail);

	@Mapping(target = "variant", source = "variant", qualifiedByName = "domainToDetail")
	InventoryItemDetail domainToDetail(InventoryItem item);

	@Mapping(target = "tags", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "modifiedDate", ignore = true)
	InventoryItemEntity domainToEntity(InventoryItem item, @MappingTarget InventoryItemEntity entity);

	@Mapping(target = "tags", ignore = true)
	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "modifiedDate", ignore = true)
	InventoryItemEntity domainToEntity(InventoryItem item);

	@Mapping(target = "createdDate", qualifiedByName = "fromUTCToLocal")
	@Mapping(target = "modifiedDate", qualifiedByName = "fromUTCToLocal")
	InventoryItem entityToDomain(InventoryItemEntity entity, @Context CycleAvoidingMappingContext context);

	@Mapping(target = "createdDate", qualifiedByName = "fromUTCToLocal")
	@Mapping(target = "modifiedDate", qualifiedByName = "fromUTCToLocal")
	@Mapping(target = "variant", qualifiedByName = "entityToBasicDomain")
	InventoryItem entityToDomain(InventoryItemEntity entity);

	@Named("entityToDomainWithoutVariant")
	@Mapping(target = "variant", ignore = true)
	@Mapping(target = "artist", qualifiedByName = "basicArtistInfo")
	@Mapping(target = "createdDate", qualifiedByName = "fromUTCToLocal")
	@Mapping(target = "modifiedDate", qualifiedByName = "fromUTCToLocal")
	InventoryItem entityToDomainWithoutVariant(InventoryItemEntity entity);
	default String inventoryItemTagToTag(InventoryItemTagEntity tag) {
		if (tag == null) {
			return null;
		}

		return tag.getName();
	}

}
