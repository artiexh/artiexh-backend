package com.artiexh.data.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "shop_product")
public class ShopProductEntity {
	@EmbeddedId
	private ShopProductId id;

	@MapsId("productId")
	@ManyToOne(optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	private ProductEntity product;

	@MapsId("shopId")
	@ManyToOne(optional = false)
	@JoinColumn(name = "shop_id", nullable = false)
	private ShopEntity shop;

	@Column(name = "price_amount", nullable = false)
	private BigDecimal priceAmount;

	@Column(name = "price_unit", nullable = false, length = 3)
	private String priceUnit;
}
