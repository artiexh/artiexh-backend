package com.artiexh.model.rest.cart.response;

import com.artiexh.model.domain.DeliveryType;
import com.artiexh.model.domain.Money;
import com.artiexh.model.domain.ProductStatus;
import com.artiexh.model.domain.ProductType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	private ProductStatus status;
	private String name;
	private String thumbnailUrl;
	private Money price;
	private String description;
	private ProductType type;
	private Long remainingQuantity;
	private Long maxItemsPerOrder;
	private DeliveryType deliveryType;
	private Integer quantity;
}
