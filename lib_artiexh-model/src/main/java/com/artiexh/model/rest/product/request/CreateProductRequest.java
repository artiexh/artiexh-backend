package com.artiexh.model.rest.product.request;

import com.artiexh.model.domain.DeliveryType;
import com.artiexh.model.domain.Money;
import com.artiexh.model.domain.PaymentMethod;
import com.artiexh.model.domain.ProductAttach;
import com.artiexh.model.domain.ProductStatus;
import com.artiexh.model.domain.ProductType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

	@NotNull
	private Float weight;
}
