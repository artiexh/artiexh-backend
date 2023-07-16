package com.artiexh.data.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class ProvidedProductId implements Serializable {
	@Serial
	private static final long serialVersionUID = 650123199811833928L;

	@NotNull
	@Column(name = "business_code", nullable = false, length = 13)
	private String businessCode;

	@NotNull
	@Column(name = "base_model_id", nullable = false)
	private Long baseModelId;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		ProvidedProductId entity = (ProvidedProductId) o;
		return Objects.equals(this.businessCode, entity.businessCode) &&
			Objects.equals(this.baseModelId, entity.baseModelId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(businessCode, baseModelId);
	}
}
