package com.artiexh.data.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cart_item")
public class CartItemEntity {

	@EmbeddedId
	private CartItemId id;

	@NotNull
	@Column(name = "quantity", nullable = false)
	private Integer quantity;

	@MapsId
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_code", nullable = false)
	private ProductInventoryEntity product;

}