package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProductAttachEntity;
import com.artiexh.data.jpa.entity.ProductEntity;
import com.artiexh.model.domain.*;
import com.artiexh.model.rest.marketplace.response.ProductInSaleCampaignResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Set;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {
		ProductCategoryMapper.class,
		ProductTagMapper.class,
		ArtistMapper.class,
		ProductAttachMapper.class
	}
)
public interface ProductMapper {

	@Mapping(target = "productCode", source = "productInventory.productCode")
	@Mapping(target = "name", source = "productInventory.name")
	@Mapping(target = "thumbnailUrl", source = "productInventory.attaches", qualifiedByName = "getProductThumbnailUrl")
	@Mapping(target = "status", source = "productInventory.status")
	@Mapping(target = "price.unit", source = "priceUnit")
	@Mapping(target = "price.amount", source = "priceAmount")
	@Mapping(target = "averageRate", source = "productInventory.averageRate")
	@Mapping(target = "type", source = "productInventory.type")
	@Mapping(target = "quantity", source = "productInventory.quantity")
	@Mapping(target = "owner", source = "productInventory.owner")
	@Mapping(target = "description", source = "productInventory.description")
	@Mapping(target = "maxItemsPerOrder", source = "productInventory.maxItemsPerOrder")
	@Mapping(target = "deliveryType", source = "productInventory.deliveryType")
	@Mapping(target = "tags", source = "productInventory.tags")
	@Mapping(target = "attaches", source = "productInventory.attaches")
	@Mapping(target = "category", source = "productInventory.category")
	@Mapping(target = "paymentMethods", source = "productInventory.paymentMethods")
	@Mapping(target = "weight", source = "productInventory.weight")
	ProductInSaleCampaignResponse entityToProductInSaleResponse(ProductEntity entity);

	@Named("getProductThumbnailUrl")
	default String getThumbnailUrl(Set<ProductAttachEntity> productAttachEntities) {
		return productAttachEntities.stream()
			.filter(attachEntity -> attachEntity.getType() == ProductAttachType.THUMBNAIL.getValue())
			.findFirst()
			.map(ProductAttachEntity::getUrl)
			.orElse(null);
	}

//	default Page<ProductResponse> productPageToProductResponsePage(Page<Product> productPage) {
//		return productPage.map(this::domainToProductResponse);
//	}
//
//	ProductResponse domainToProductResponse(Product product);


	//	@Mapping(target = "price.unit", source = "priceUnit")
//	@Mapping(target = "price.amount", source = "priceAmount")
//	@Mapping(target = "thumbnailUrl", source = "attaches", qualifiedByName = "getProductThumbnailUrl")
//	@Mapping(target = "owner", qualifiedByName = "basicArtistInfo")
//	@Mapping(target = "shop", qualifiedByName = "basicShopInfo")
//	@Mapping(target = "bundles", source = "bundles", qualifiedByName = "bundleEntitiesToDomains")
//	@Mapping(target = "bundleItems", source = "bundleItems", qualifiedByName = "bundleItemEntitiesToDomains")
//	@Mapping(target = "productInCampaign.id", source = "productInCampaignId")
//	Product entityToDomain(ProductEntity productEntity);

//	@Named("bundleEntitiesToDomains")
//	@IterableMapping(qualifiedByName = "bundleEntityToDomain")
//	Set<Product> bundleEntitiesToDomains(Set<ProductEntity> productEntities);

//	@Named("bundleEntityToDomain")
//	@Mapping(target = "price.unit", source = "priceUnit")
//	@Mapping(target = "price.amount", source = "priceAmount")
//	@Mapping(target = "thumbnailUrl", source = "attaches", qualifiedByName = "getProductThumbnailUrl")
//	@Mapping(target = "owner", qualifiedByName = "basicArtistInfo")
//	@Mapping(target = "shop", qualifiedByName = "basicShopInfo")
//	@Mapping(target = "bundleItems", ignore = true)
//	@Mapping(target = "productInCampaign.id", source = "productInCampaignId")
//	Product bundleEntityToDomain(ProductEntity productEntity);

//	@Named("bundleItemEntitiesToDomains")
//	@IterableMapping(qualifiedByName = "bundleItemEntityToDomain")
//	Set<Product> bundleItemEntitiesToDomains(Set<ProductEntity> productEntities);

//	@Named("bundleItemEntityToDomain")
//	@Mapping(target = "price.unit", source = "priceUnit")
//	@Mapping(target = "price.amount", source = "priceAmount")
//	@Mapping(target = "thumbnailUrl", source = "attaches", qualifiedByName = "getProductThumbnailUrl")
//	@Mapping(target = "owner", qualifiedByName = "basicArtistInfo")
//	@Mapping(target = "shop", qualifiedByName = "basicShopInfo")
//	@Mapping(target = "bundles", ignore = true)
//	@Mapping(target = "productInCampaign.id", source = "productInCampaignId")
//	Product bundleItemEntityToDomain(ProductEntity productEntity);

	//	@Mapping(target = "priceUnit", source = "price.unit")
//	@Mapping(target = "priceAmount", source = "price.amount")
//	@Mapping(target = "averageRate", constant = "0f")
//	@Mapping(target = "productInCampaignId", source = "productInCampaign.id")
//	ProductEntity domainToEntity(Product product);
//
//	Product documentToDomain(ProductDocument productDocument);
//
//	ProductDocument domainToDocument(Product product);

//	@Mapping(target = "price.unit", source = "priceUnit")
//	@Mapping(target = "price.amount", source = "priceAmount")
//	ProductDocument entityToDocument(ProductEntity productEntity);

	//	@Mapping(target = "category.id", source = "categoryId")
//	@Mapping(target = "bundleItems", source = "bundleItems", qualifiedByName = "bundleItemsToProductSet")
//	Product createProductRequestToProduct(CreateProductRequest createProductRequest);
//
//	@Mapping(target = "productInCampaign.id", source = "productInCampaignId")
//		//@Mapping(target = "bundleItems", source = "bundleItems", qualifiedByName = "bundleItemsToProductSet")
//	Product publishProductRequestToProduct(FinalizeProductRequest productRequest);
//
//	@Mapping(target = "productInCampaign.id", source = "id")
//	@Mapping(target = "category", source = "customProduct.category")
//	@Mapping(target = "tags", source = "customProduct.tags")
//	Product productInCampaignToProduct(ProductInCampaign productInCampaign);
//
//	@Named("bundleItemsToProductSet")
//	default Set<Product> bundleItemsToProductSet(Set<Long> bundleItems) {
//		if (bundleItems == null) {
//			return null;
//		}
//
//		return bundleItems.stream()
//			.map(id -> Product.builder().id(id).build())
//			.collect(Collectors.toSet());
//	}
//
//	@Mapping(target = "category.id", source = "categoryId")
//	@Mapping(target = "bundleItems", source = "bundleItems", qualifiedByName = "bundleItemsToProductSet")
//	Product updateProductRequestToProduct(UpdateProductRequest updateProductRequest);

	default Integer toValue(ProductStatus status) {
		return status.getValue();
	}

	default ProductStatus toProductStatus(Integer value) {
		return ProductStatus.fromValue(value);
	}

	default Integer toValue(ProductType type) {
		return type.getValue();
	}

	default ProductType toProductType(Integer value) {
		return ProductType.fromValue(value);
	}

	default Integer toValue(PaymentMethod paymentMethod) {
		return paymentMethod.getValue();
	}

	default PaymentMethod toPaymentMethod(Integer value) {
		return PaymentMethod.fromValue(value);
	}

	default Integer toValue(DeliveryType type) {
		return type.getValue();
	}

	default DeliveryType toDeliveryType(Integer value) {
		return DeliveryType.fromValue(value);
	}
}
