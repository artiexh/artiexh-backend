package com.artiexh.model.rest.order.response;

import com.artiexh.model.domain.DeliveryType;
import com.artiexh.model.domain.Money;
import com.artiexh.model.domain.ProductStatus;
import com.artiexh.model.domain.ProductType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailResponse {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	private ProductStatus status;
	private String name;
	private String thumbnailUrl;
	private Money price;
	private String description;
	private ProductType type;
	private Long remainingQuantity;
	private Instant publishDatetime;
	private Long maxItemsPerOrder;
	private DeliveryType deliveryType;
	private Integer quantity;
}
