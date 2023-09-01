package com.artiexh.data.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class OrderDetailId implements Serializable {
	@Serial
	private static final long serialVersionUID = 5632503753624783408L;

	@NotNull
	@Column(name = "order_id", nullable = false)
	private Long orderId;

	@NotNull
	@Column(name = "product_id", nullable = false)
	private Long productId;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		OrderDetailId entity = (OrderDetailId) o;
		return Objects.equals(this.productId, entity.productId) &&
			Objects.equals(this.orderId, entity.orderId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(productId, orderId);
	}

}