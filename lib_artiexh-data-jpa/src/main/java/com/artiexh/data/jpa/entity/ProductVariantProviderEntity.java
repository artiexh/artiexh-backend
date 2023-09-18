package com.artiexh.data.jpa.entity;

import com.artiexh.data.jpa.entity.embededmodel.ProductVariantProviderId;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_variant_provider_mapping")
@Builder(toBuilder = true)
public class ProductVariantProviderEntity {
	@EmbeddedId
	private ProductVariantProviderId id;

	@MapsId("businessCode")
	@ManyToOne()
	@JoinColumn(name = "business_code")
	private ProviderEntity provider;

	@Column(name = "base_price_amount", nullable = false)
	private BigDecimal basePriceAmount;
}
