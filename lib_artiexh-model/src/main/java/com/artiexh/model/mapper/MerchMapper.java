package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.MerchCategoryEntity;
import com.artiexh.data.jpa.entity.MerchEntity;
import com.artiexh.model.domain.Merch;
import com.artiexh.model.rest.product.request.UpdateProductRequest;
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
public interface MerchMapper {

	//	@Mapping(target = "categoryInfo", source = "categories", qualifiedByName = "categoryMapping")
	@Mapping(target = "ownerInfo", source = "owner")
	@Mapping(target = "owner.merch", ignore = true)
	@Mapping(target = "owner.province.country.provinces", ignore = true)
	Merch entityToDomainModel(MerchEntity merchEntity);

	Merch requestToDomainModel(UpdateProductRequest requestToDomainModel);

	List<Merch> entitiesToDomainModels(List<MerchEntity> merchEntity);

	MerchEntity domainModelToEntity(Merch merch);

	@Mapping(target = "owner", ignore = true)
	MerchEntity domainModelToEntity(Merch merch, @MappingTarget MerchEntity entity);

	@Named("categoryMapping")
	default Map<Long, String> categoryMapping(Set<MerchCategoryEntity> categoryEntities) {
		Map<Long, String> categories;
		categories = categoryEntities.stream()
			.collect(Collectors.toMap(MerchCategoryEntity::getId, MerchCategoryEntity::getName));
		return categories;
	}
}
