package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "provided_model")
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
public class ProvidedModelEntity {

	@EmbeddedId
	private ProvidedModelId id;

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

	@Type(JsonType.class)
	@Column(name = "color", columnDefinition = "json")
	private Color color;

	@Type(JsonType.class)
	@Column(name = "sizes", columnDefinition = "json")
	private List<Size> sizes;

	@Column(name = "max_limit")
	private Long maxLimit;

	@Type(JsonType.class)
	@Column(name = "allow_config", columnDefinition = "json")
	private String[] allowConfig;

	@Column(name = "provided_model_file_url")
	private String providedModelFileUrl;
}
