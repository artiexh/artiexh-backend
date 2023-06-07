package com.artiexh.model.product;

import com.artiexh.data.jpa.entity.MerchAttachEntity;
import com.artiexh.data.jpa.entity.MerchCategoryEntity;
import com.artiexh.data.jpa.entity.MerchEntity;
import com.artiexh.model.domain.MerchAttachType;
import com.artiexh.model.mapper.*;
import com.artiexh.model.product.request.UpdateProductRequest;
import org.mapstruct.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {
		UserStatusMapper.class,
		RoleMapper.class,
		MerchStatusMapper.class,
		MerchTypeMapper.class,
		DeliveryTypeMapper.class,
		MerchCategoryMapper.class,
		MerchTagMapper.class,
		MerchAttachTypeMapper.class,
		ArtistMapper.class
	}
)
public interface ProductMapper {

	@Mapping(target = "categoryInfo", source = "categories", qualifiedByName = "categoryMapping")
	@Mapping(target = "ownerInfo", source = "owner")
	ProductDetail entityToModelDetail(MerchEntity merchEntity);

	@Named("entityToModelInfo")
	@Mapping(target = "ownerInfo", source = "owner")
	@Mapping(target = "id", source = "id")
	@Mapping(target = "thumbnailUrl", source = "attaches", qualifiedByName = "attachmentMapping")
	ProductInfo entityToModelInfo(MerchEntity merchEntity);

	ProductDetail requestToDomainModel(UpdateProductRequest requestToDomainModel);

	@IterableMapping(qualifiedByName = "entityToModelInfo")
	List<ProductInfo> entitiesToDomainModels(List<MerchEntity> merchEntity);

	MerchEntity domainModelToEntity(ProductDetail merch);

	@Mapping(target = "owner", ignore = true)
	MerchEntity domainModelToEntity(ProductDetail merch, @MappingTarget MerchEntity entity);

	@Named("categoryMapping")
	default Map<Long, String> categoryMapping(Set<MerchCategoryEntity> categoryEntities) {
		Map<Long, String> categories;
		categories = categoryEntities.stream()
			.collect(Collectors.toMap(MerchCategoryEntity::getId, MerchCategoryEntity::getName));
		return categories;
	}

	@Named("attachmentMapping")
	default String attachmentMapping(Set<MerchAttachEntity> attachments) {
		MerchAttachEntity attachEntity = attachments.stream()
			.filter(attachment ->
				MerchAttachType.THUMBNAIL.getValue() == attachment.getType().intValue())
			.collect(Collectors.toList()).get(0);
		if (attachEntity == null) {
			return null;
		} else {
			return attachEntity.getUrl();
		}
	}
}
