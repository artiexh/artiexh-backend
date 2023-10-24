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
@Embeddable
@Builder
public class OrderHistoryEntityId implements Serializable {
	@Serial
	private static final long serialVersionUID = 6651597644338920273L;

	@NotNull
	@Column(name = "order_id", nullable = false)
	private Long orderId;

	@NotNull
	@Column(name = "status", nullable = false)
	private Byte status;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		OrderHistoryEntityId entity = (OrderHistoryEntityId) o;
		return Objects.equals(this.orderId, entity.orderId) &&
			Objects.equals(this.status, entity.status);
	}

	@Override
	public int hashCode() {
		return Objects.hash(orderId, status);
	}

}