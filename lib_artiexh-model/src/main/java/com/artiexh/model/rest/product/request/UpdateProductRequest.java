package com.artiexh.model.rest.product.request;

import com.artiexh.model.domain.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {
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

	@NotNull
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
