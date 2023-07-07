package com.artiexh.data.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "provided_base_product")
@Builder(toBuilder = true)
public class ProvidedProductEntity {

	@EmbeddedId
	private ProvidedProductId id;

	@Column(name = "price_amount", nullable = false)
	private BigDecimal priceAmount;

	@Column(name = "price_unit", nullable = false, length = 3)
	private String priceUnit;

	private String description;
}
