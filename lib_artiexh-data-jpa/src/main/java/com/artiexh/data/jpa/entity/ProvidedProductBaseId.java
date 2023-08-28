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
public class ProvidedProductBaseId implements Serializable {
	@Serial
	private static final long serialVersionUID = 650123199811833928L;

	@NotNull
	@Column(name = "business_code", nullable = false, length = 13)
	private String businessCode;

	@NotNull
	@Column(name = "product_base_id", nullable = false)
	private Long productBaseId;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		ProvidedProductBaseId entity = (ProvidedProductBaseId) o;
		return Objects.equals(this.businessCode, entity.businessCode) &&
			Objects.equals(this.productBaseId, entity.productBaseId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(businessCode, productBaseId);
	}
}
