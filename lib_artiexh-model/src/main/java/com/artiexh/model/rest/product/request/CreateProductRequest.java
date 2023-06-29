package com.artiexh.model.rest.product.request;

import com.artiexh.model.domain.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
	@NotNull
	private ProductStatus status;

	@NotBlank
	private String name;

	@Valid
	private Money price;

	@NotNull
	private Long categoryId;

	@NotBlank
	private String description;

	@NotNull
	private ProductType type;

	@NotNull
	@Min(0)
	private Long remainingQuantity;

	@Future
	private Instant publishDatetime;

	private Integer maxItemsPerOrder;

	@NotNull
	private DeliveryType deliveryType;

	@NotEmpty
	private Set<PaymentMethod> paymentMethods;

	private Set<String> tags;

	@Valid
	private Set<ProductAttach> attaches;
}
