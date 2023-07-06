package com.artiexh.model.rest.cart.response;

import com.artiexh.model.domain.DeliveryType;
import com.artiexh.model.domain.ProductAttach;
import com.artiexh.model.domain.ProductStatus;
import com.artiexh.model.domain.ProductType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	private ProductStatus status;
	private String currency;
	private String name;
	private BigDecimal price;
	private String description;
	private ProductType type;
	private Long remainingQuantity;
	private Instant publishDatetime;
	private Long maxItemsPerOrder;
	private DeliveryType deliveryType;
	private Integer quantity;
	private Set<ProductAttach> attaches;
}
