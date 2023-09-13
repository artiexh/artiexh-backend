package com.artiexh.data.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_variant_combination")
@Builder(toBuilder = true)
public class ProductVariantCombinationEntity {

	@EmbeddedId
	private ProductVariantCombinationEntityId id;
	@Column(name = "option_id")
	private Long optionId;
	private Integer quantity;
}
