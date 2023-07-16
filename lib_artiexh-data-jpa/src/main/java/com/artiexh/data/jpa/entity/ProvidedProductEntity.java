package com.artiexh.data.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

	@MapsId("baseModelId")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "base_model_id", nullable = false)
	private BaseModelEntity baseModel;

	@MapsId("businessCode")
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "business_code", nullable = false)
	private ProviderEntity provider;

	@Column(name = "price_amount", nullable = false)
	private BigDecimal priceAmount;

	@Column(name = "price_unit", nullable = false, length = 3)
	private String priceUnit;

	private String description;


}
