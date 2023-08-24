package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "order")
public class OrderEntity {
	@Id
	@Tsid
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "shop_id", nullable = false)
	private ArtistEntity shop;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "shipping_address_id", nullable = false)
	private UserAddressEntity shippingAddress;

	@Column(name = "note")
	private String note;

	@Column(name = "payment_method", nullable = false)
	private Byte paymentMethod;

	@Column(name = "status", nullable = false)
	private Byte status;

	@OneToMany
	@JoinColumn(name = "order_id")
	private Set<OrderDetailEntity> orderDetails = new LinkedHashSet<>();

	@NotNull
	@Column(name = "modified_date", nullable = false)
	private LocalDateTime modifiedDate;

	@NotNull
	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate;

}