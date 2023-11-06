package com.artiexh.data.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailEntityId implements Serializable {
	@Serial
	private static final long serialVersionUID = 426241825757468637L;
	private Long campaignOrder;
	private Long product;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		OrderDetailEntityId that = (OrderDetailEntityId) o;
		return Objects.equals(campaignOrder, that.campaignOrder) && Objects.equals(product, that.product);
	}

	@Override
	public int hashCode() {
		return Objects.hash(campaignOrder, product);
	}
}