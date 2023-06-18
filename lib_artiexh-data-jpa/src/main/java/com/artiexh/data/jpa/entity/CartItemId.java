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
public class CartItemId implements Serializable {

	@Serial
	private static final long serialVersionUID = 720123199811833928L;

	@NotNull
	@Column(name = "cart_id", nullable = false)
	private Long cartId;

	@NotNull
	@Column(name = "merch_id", nullable = false)
    private Long merchId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		CartItemId entity = (CartItemId) o;
        return Objects.equals(this.cartId, entity.cartId) &&
                Objects.equals(this.merchId, entity.merchId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId, merchId);
    }

}