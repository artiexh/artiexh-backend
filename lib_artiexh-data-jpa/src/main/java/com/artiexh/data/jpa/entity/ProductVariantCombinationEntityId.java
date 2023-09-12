package com.artiexh.data.jpa.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProductVariantCombinationEntityId implements Serializable {
	private Long variantId;
	private Long optionId;
	private Long optionValueId;
}
