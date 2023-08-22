package com.artiexh.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Product {
	private Long id;
	private Artist owner;
	private ProductStatus status;
	private String name;
	private Money price;
	private String thumbnailUrl;
	private ProductCategory category;
	private String description;
	private ProductType type;
	private Integer remainingQuantity;
	private Instant publishDatetime;
	private Long maxItemsPerOrder;
	private DeliveryType deliveryType;
	private Float averageRate;
	private Set<Byte> paymentMethods;
	private Set<ProductTag> tags;
	private Set<ProductAttach> attaches;
}
