package com.artiexh.model.rest.marketplace.request;

import com.artiexh.model.domain.Money;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInSaleRequest {
	@NotNull
	private String productCode;
	@NotNull
	@Min(1)
	private Integer quantity;
	@Valid
	private Money price;
}
