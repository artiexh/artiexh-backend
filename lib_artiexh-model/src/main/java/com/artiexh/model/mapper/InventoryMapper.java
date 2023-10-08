package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.InventoryItemEntity;
import com.artiexh.data.jpa.entity.InventoryItemTagEntity;
import com.artiexh.model.domain.InventoryItem;
import com.artiexh.model.rest.inventory.InventoryItemDetail;
import org.mapstruct.*;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {MediaMapper.class, ArtistMapper.class, ProductVariantMapper.class}
)
public interface InventoryMapper {
	@Mapping(target = "artist", source = "artistId", qualifiedByName = "idToDomain")
	@Mapping(target = "variant", source = "variantId", qualifiedByName = "idToDomain")
	InventoryItem detailToDomain(InventoryItemDetail detail);

	@Mapping(target = "variant", source = "variant", qualifiedByName = "domainToDetail")
	InventoryItemDetail domainToDetail(InventoryItem item);

	@Mapping(target = "tags", ignore = true)
	InventoryItemEntity domainToEntity(InventoryItem item, @MappingTarget InventoryItemEntity entity);

	@Mapping(target = "tags", ignore = true)
	InventoryItemEntity domainToEntity(InventoryItem item);

	InventoryItem entityToDomain(InventoryItemEntity entity, @Context CycleAvoidingMappingContext context);

	@Named("entityToDomainWithoutVariant")
	@Mapping(target = "variant", ignore = true)
	@Mapping(target = "artist", qualifiedByName = "basicArtistInfo")
	InventoryItem entityToDomainWithoutVariant(InventoryItemEntity entity);
	default String inventoryItemTagToTag(InventoryItemTagEntity tag) {
		if (tag == null) {
			return null;
		}

		return tag.getName();
	}

}
