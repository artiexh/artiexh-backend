package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "provided_product")
@Builder(toBuilder = true)
public class ProvidedProductBaseEntity {
	@EmbeddedId
	private ProvidedProductBaseId id;

	@MapsId("baseModelId")
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "product_base_id", nullable = false)
	private ProductBaseEntity productBase;

	@MapsId("businessCode")
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "business_code", nullable = false)
	private ProviderEntity provider;

	@Column(name = "price_amount", nullable = false)
	private BigDecimal priceAmount;

	@Column(name = "price_unit", nullable = false, length = 3)
	private String priceUnit;

	@Column(name = "description", nullable = false)
	private String description;

	@Type(JsonType.class)
	@Column(name = "color", columnDefinition = "json", nullable = false)
	private Color color;

	@Type(JsonType.class)
	@Column(name = "sizes", columnDefinition = "json", nullable = false)
	private List<Size> sizes;

	@Column(name = "max_limit", nullable = false)
	private Long maxLimit;

	@Type(JsonType.class)
	@Column(name = "allow_config", columnDefinition = "json", nullable = false)
	private String[] allowConfig;

	@Column(name = "provided_product_file_url", nullable = false)
	private String providedProductFileUrl;
}
