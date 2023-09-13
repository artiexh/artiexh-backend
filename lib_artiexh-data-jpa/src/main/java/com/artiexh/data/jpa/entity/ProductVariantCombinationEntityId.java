package com.artiexh.data.jpa.entity;

import jakarta.persistence.Column;
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

	@Column(name = "variant_id")
	private Long variantId;
	@Column(name = "option_value_id")
	private Long optionValueId;
}
