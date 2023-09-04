package com.artiexh.data.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class OrderHistoryId implements Serializable {
	@Serial
	private static final long serialVersionUID = 1630751995511086698L;

	@NotNull
	@Column(name = "oder_id", nullable = false)
	private Long oderId;

	@NotNull
	@Column(name = "status", nullable = false)
	private Byte status;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		OrderHistoryId entity = (OrderHistoryId) o;
		return Objects.equals(this.oderId, entity.oderId) &&
			Objects.equals(this.status, entity.status);
	}

	@Override
	public int hashCode() {
		return Objects.hash(oderId, status);
	}

}