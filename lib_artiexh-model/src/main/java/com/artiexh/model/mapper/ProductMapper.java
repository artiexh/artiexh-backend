package com.artiexh.model.mapper;

import com.artiexh.data.elasticsearch.model.ProductDocument;
import com.artiexh.data.jpa.entity.ProductEntity;
import com.artiexh.model.domain.*;
import com.artiexh.model.rest.product.request.CreateProductRequest;
import com.artiexh.model.rest.product.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

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

	Product entityToDomain(ProductEntity productEntity);

	Product documentToDomain(ProductDocument productDocument);

	ProductResponse domainToProductResponse(Product product);

	default Page<ProductResponse> domainPageToProductResponsePage(Page<Product> product) {
		return product.map(this::domainToProductResponse);
	}

	Product createProductRequestToProduct(CreateProductRequest createProductRequest);

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
