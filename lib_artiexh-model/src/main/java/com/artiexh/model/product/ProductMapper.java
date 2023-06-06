package com.artiexh.model.product;

import com.artiexh.data.jpa.entity.MerchCategoryEntity;
import com.artiexh.data.jpa.entity.MerchEntity;
import com.artiexh.model.domain.Merch;
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
	@Mapping(target = "ownerInfo", source = "owner")
	ProductInfo entityToModelInfo(MerchEntity merchEntity);

	ProductDetail requestToDomainModel(UpdateProductRequest requestToDomainModel);

	List<ProductInfo> entitiesToDomainModels(List<MerchEntity> merchEntity);

	MerchEntity domainModelToEntity(ProductDetail merch);

	@Mapping(target = "owner", ignore = true)
	MerchEntity domainModelToEntity(ProductDetail merch, @MappingTarget MerchEntity entity);

	@Named("categoryMapping")
	default Map<Long, String> categoryMapping (Set<MerchCategoryEntity> categoryEntities) {
		Map<Long, String> categories;
		categories = categoryEntities.stream()
			.collect(Collectors.toMap(MerchCategoryEntity::getId, MerchCategoryEntity::getName));
		return categories;
	}
}
