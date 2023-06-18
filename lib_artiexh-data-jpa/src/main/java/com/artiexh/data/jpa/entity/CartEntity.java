package com.artiexh.data.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@Table(name = "cart")
@NoArgsConstructor
@AllArgsConstructor
public class CartEntity {

	@Id
	@Column(name = "user_id", nullable = false)
	private Long id;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "cart_id")
	private Set<CartItemEntity> cartItems;

}