package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProductAttachEntity;
import com.artiexh.data.jpa.entity.ProductEntity;
import com.artiexh.data.opensearch.model.ProductDocument;
import com.artiexh.model.domain.*;
import com.artiexh.model.rest.product.request.CreateProductRequest;
import com.artiexh.model.rest.product.request.UpdateProductRequest;
import com.artiexh.model.rest.product.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {
		AccountMapper.class,
		ProductCategoryMapper.class,
		ProductTagMapper.class,
		ArtistMapper.class,
		ProductAttachMapper.class,
		ShopMapper.class
	}
)
public interface ProductMapper {

	default Page<ProductResponse> productPageToProductResponsePage(Page<Product> productPage) {
		return productPage.map(this::domainToProductResponse);
	}

	ProductResponse domainToProductResponse(Product product);


	@Mapping(target = "price.unit", source = "priceUnit")
	@Mapping(target = "price.amount", source = "priceAmount")
	@Mapping(target = "thumbnailUrl", source = "attaches", qualifiedByName = "getProductThumbnailUrl")
	@Mapping(target = "owner", qualifiedByName = "basicArtistInfo")
	@Mapping(target = "shop", qualifiedByName = "basicShopInfo")
	Product entityToDomain(ProductEntity productEntity);

	@Named("getProductThumbnailUrl")
	default String getThumbnailUrl(Set<ProductAttachEntity> productAttachEntities) {
		return productAttachEntities.stream()
			.filter(attachEntity -> attachEntity.getType() == ProductAttachType.THUMBNAIL.getValue())
			.findFirst()
			.map(ProductAttachEntity::getUrl)
			.orElse(null);
	}

	@Mapping(target = "priceUnit", source = "price.unit")
	@Mapping(target = "priceAmount", source = "price.amount")
	@Mapping(target = "averageRate", constant = "0f")
	ProductEntity domainToEntity(Product product);

	Product documentToDomain(ProductDocument productDocument);

	@Mapping(target = "price.unit", source = "priceUnit")
	@Mapping(target = "price.amount", source = "priceAmount")
	ProductDocument entityToDocument(ProductEntity productEntity);

	@Mapping(target = "category.id", source = "categoryId")
	@Mapping(target = "bundleItems", source = "bundleItems", qualifiedByName = "bundleItemsToProductSet")
	Product createProductRequestToProduct(CreateProductRequest createProductRequest);

	@Named("bundleItemsToProductSet")
	default Set<Product> bundleItemsToProductSet(Set<Long> bundleItems) {
		if (bundleItems == null) {
			return null;
		}

		return bundleItems.stream()
			.map(id -> Product.builder().id(id).build())
			.collect(Collectors.toSet());
	}

	@Mapping(target = "category.id", source = "categoryId")
	Product updateProductRequestToProduct(UpdateProductRequest updateProductRequest);

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
