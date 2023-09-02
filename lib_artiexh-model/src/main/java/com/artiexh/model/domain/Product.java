package com.artiexh.model.domain;

import java.time.Instant;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Product {

	private Long id;
	private Artist owner;
	private ProductStatus status;
	private String name;
	private String thumbnailUrl;
	private Money price;
	private ProductCategory category;
	private String description;
	private ProductType type;
	private Long remainingQuantity;
	private Instant publishDatetime;
	private Long maxItemsPerOrder;
	private DeliveryType deliveryType;
	private Float averageRate;
	private Set<Byte> paymentMethods;
	private Set<ProductTag> tags;
	private Set<ProductAttach> attaches;
	private Shop shop;
	private Float weight;

}
