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
public class ShopProductId implements Serializable {
	@Serial
	private static final long serialVersionUID = 720223197811833929L;

	@NotNull
	@Column(name = "shop_id", nullable = false)
	private Long shopId;

	@NotNull
	@Column(name = "product_id", nullable = false)
	private Long productId;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		ShopProductId entity = (ShopProductId) o;
		return Objects.equals(this.shopId, entity.shopId) &&
			Objects.equals(this.productId, entity.productId);
	}
}
