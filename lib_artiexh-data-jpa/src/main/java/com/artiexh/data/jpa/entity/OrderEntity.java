package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "`order`")
@EntityListeners(AuditingEntityListener.class)
public class OrderEntity extends BaseAuditEntity {

	@Id
	@Tsid
	@Column(name = "id", nullable = false)
	private Long id;

//	@ManyToOne(fetch = FetchType.LAZY, optional = false)
//	@JoinColumn(name = "user_id", nullable = false)
//	private UserEntity user;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "shop_id", nullable = false)
	private ArtistEntity shop;

//	@ManyToOne(fetch = FetchType.LAZY, optional = false)
//	@JoinColumn(name = "shipping_address_id", nullable = false)
//	private UserAddressEntity shippingAddress;

	@Column(name = "note")
	private String note;

	@Column(name = "payment_method", nullable = false)
	private Byte paymentMethod;

	@Column(name = "status", nullable = false)
	private Byte status;

	@OneToMany
	@JoinColumn(name = "order_id")
	private Set<OrderDetailEntity> orderDetails = new LinkedHashSet<>();

//	@OneToMany
//	@JoinColumn(name = "order_id")
//	private Set<OrderTransactionEntity> orderTransaction = new LinkedHashSet<>();

	@Column(name = "order_group_id")
	private Long orderGroupId;

	@Column(name = "shipping_fee", nullable = false)
	private BigDecimal shippingFee;
}