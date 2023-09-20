package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ImageSetEntity;
import com.artiexh.data.jpa.entity.InventoryItemEntity;
import com.artiexh.model.domain.ImageSet;
import com.artiexh.model.domain.InventoryItem;
import com.artiexh.model.rest.inventory.InventoryItemDetail;
import org.mapstruct.*;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {MediaMapper.class, ArtistMapper.class, ProductVariantMapper.class }
)
public interface InventoryMapper {
	@Mapping(target = "artist", source = "artistId", qualifiedByName = "idToDomain")
	@Mapping(target = "variant", source = "variantId", qualifiedByName = "idToDomain")
	InventoryItem detailToDomain(InventoryItemDetail detail);

	InventoryItemDetail domainToDetail(InventoryItem item);

	InventoryItemEntity domainToEntity(InventoryItem item, @MappingTarget InventoryItemEntity entity);

	InventoryItemEntity domainToEntity(InventoryItem item);

	InventoryItem entityToDomain(InventoryItemEntity entity, @Context CycleAvoidingMappingContext context);


}
