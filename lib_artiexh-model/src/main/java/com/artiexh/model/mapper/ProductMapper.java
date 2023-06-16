package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.MerchAttachEntity;
import com.artiexh.data.jpa.entity.MerchEntity;
import com.artiexh.data.jpa.entity.PreOrderMerchEntity;
import com.artiexh.model.domain.MerchAttachType;
import com.artiexh.model.rest.product.ProductDetail;
import com.artiexh.model.rest.product.ProductInfo;
import com.artiexh.model.rest.product.request.UpdateProductRequest;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {
		UserStatusMapper.class,
		RoleMapper.class,
		MerchStatusMapper.class,
		PaymentMethodMapper.class,
		MerchTypeMapper.class,
		DeliveryTypeMapper.class,
		MerchCategoryMapper.class,
		MerchTagMapper.class,
		MerchAttachTypeMapper.class,
		ArtistMapper.class
	}
)
public interface ProductMapper {

	//	@Mapping(target = "categoryInfo", source = "categories", qualifiedByName = "categoryMapping")
	@Mapping(target = "ownerInfo", source = "owner")
	@Mapping(target = "categoryInfo", source = "category")
	ProductDetail entityToModelDetail(MerchEntity merchEntity);

	@Mapping(target = "ownerInfo", source = "owner")
	@Mapping(target = "categoryInfo", source = "category")
	ProductDetail entityToModelDetail(PreOrderMerchEntity merchEntity);

	@Mapping(target = "ownerInfo", source = "owner")
	@Mapping(target = "categoryInfo", source = "category")
	ProductDetail entityToModelDetail(PreOrderMerchEntity preOrderMerchEntity, @MappingTarget ProductDetail productDetail);

	@Mapping(target = "ownerInfo", source = "owner")
	@Mapping(target = "categoryInfo", source = "category")
	@Mapping(target = "paymentMethod", source = "paymentMethod")
	ProductDetail entityToModelDetail(MerchEntity merchEntity, @MappingTarget ProductDetail productDetail);

	@Named("entityToModelInfo")
	@Mapping(target = "ownerInfo", source = "owner")
	@Mapping(target = "id", source = "id")
	@Mapping(target = "thumbnailUrl", source = "attaches", qualifiedByName = "attachmentMapping")
	ProductInfo entityToModelInfo(MerchEntity merchEntity);

	ProductDetail requestToDomainModel(UpdateProductRequest requestToDomainModel);

	@IterableMapping(qualifiedByName = "entityToModelInfo")
	List<ProductInfo> entitiesToDomainModels(List<MerchEntity> merchEntity);

	@Mapping(target = "averageRate", source = "averageRate", ignore = true)
	MerchEntity domainModelToEntity(ProductDetail merch);

	@Mapping(target = "owner", ignore = true)
	@Mapping(target = "averageRate", source = "averageRate", ignore = true)
	MerchEntity domainModelToEntity(ProductDetail merch, @MappingTarget MerchEntity entity);

	@Mapping(target = "owner", ignore = true)
	@Mapping(target = "averageRate", source = "averageRate", ignore = true)
	PreOrderMerchEntity domainModelToEntity(ProductDetail merch, @MappingTarget PreOrderMerchEntity entity);

	@Named("attachmentMapping")
	default String attachmentMapping(Set<MerchAttachEntity> attachments) {
		MerchAttachEntity attachEntity = attachments.stream()
			.filter(attachment ->
				MerchAttachType.THUMBNAIL.getValue() == attachment.getType().intValue())
			.toList().get(0);
		if (attachEntity == null) {
			return null;
		} else {
			return attachEntity.getUrl();
		}
	}
}
