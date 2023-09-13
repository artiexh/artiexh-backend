package com.artiexh.data.jpa.entity;

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
	private Integer quantity;
}
