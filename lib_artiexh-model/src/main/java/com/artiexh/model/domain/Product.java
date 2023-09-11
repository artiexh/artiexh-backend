package com.artiexh.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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
	private String thumbnailUrl;
	private Money price;
	private ProductCategory category;
	private String description;
	private ProductType type;
	private Long remainingQuantity;
	private Long maxItemsPerOrder;
	private DeliveryType deliveryType;
	private Float averageRate;
	private Set<Byte> paymentMethods;
	private Set<ProductTag> tags;
	private Set<ProductAttach> attaches;
	private Shop shop;
	private Float weight;

}
