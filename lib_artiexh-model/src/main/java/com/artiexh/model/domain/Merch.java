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
public class Merch {
	private Long id;
	private Artist owner;
	private MerchStatus status;
	private String name;
	private Double price;
	private String description;
	private MerchType type;
	private Long remainingQuantity;
	private Instant publishDatetime;
	private Long maxItemsPerOrder;
	private DeliveryType deliveryType;
	private Set<String> categories;
	private Set<String> tags;
	private Set<MerchAttach> attaches;
}
