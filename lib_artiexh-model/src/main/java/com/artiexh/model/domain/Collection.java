package com.artiexh.model.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Collection {
	private Long id;
	private String name;
	private String imageUrl;
	private BigDecimal priceAmount;
	private Set<ProvidedProductBase> providedProducts;
}
