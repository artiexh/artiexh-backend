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
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

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

	@Mapping(target = "price.unit", source = "priceUnit")
	@Mapping(target = "price.amount", source = "priceAmount")
	@Mapping(target = "thumbnailUrl", source = "attaches")
	Product entityToDomain(ProductEntity productEntity);

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

	ProductResponse domainToProductResponse(Product product);

	@Mapping(target = "price.unit", source = "priceUnit")
	@Mapping(target = "price.amount", source = "priceAmount")
	ProductDocument entityToDocument(ProductEntity productEntity);

	default Page<ProductResponse> domainPageToProductResponsePage(Page<Product> productPage) {
		return productPage.map(this::domainToProductResponse);
	}

	@Mapping(target = "category.id", source = "categoryId")
	Product createProductRequestToProduct(CreateProductRequest createProductRequest);

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
