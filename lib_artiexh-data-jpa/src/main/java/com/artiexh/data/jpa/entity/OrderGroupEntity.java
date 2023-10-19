package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_group")
@EntityListeners(AuditingEntityListener.class)
public class OrderGroupEntity extends BaseAuditEntity {
	@Id
	@Tsid
	@Column(name = "id", nullable = false)
	private Long id;

	@Builder.Default
	@OneToMany
	@JoinColumn(name = "order_group_id")
	private Set<OrderEntity> orders = new LinkedHashSet<>();

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "shipping_address_id", nullable = false)
	private UserAddressEntity shippingAddress;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private UserEntity user;

	@Builder.Default
	@OneToMany
	@JoinColumn(name = "order_group_id")
	private Set<OrderTransactionEntity> orderTransaction = new LinkedHashSet<>();

	@Column(name = "payment_method", nullable = false)
	private Byte paymentMethod;

	@Column(name = "delivery_address")
	private String deliveryAddress;

	@Column(name = "delivery_ward")
	private String deliveryWard;

	@Column(name = "delivery_district")
	private String deliveryDistrict;

	@Column(name = "delivery_province")
	private String deliveryProvince;

	@Column(name = "delivery_country")
	private String deliveryCountry;

	@Column(name = "delivery_tel", length = 15)
	private String deliveryTel;

	@Column(name = "delivery_email", length = 254)
	private String deliveryEmail;

}
