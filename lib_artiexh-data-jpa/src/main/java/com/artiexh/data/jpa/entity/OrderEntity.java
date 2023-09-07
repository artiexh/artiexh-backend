package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
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

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "shop_id", nullable = false)
	private ArtistEntity shop;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_group_id", insertable = false, updatable = false)
	private OrderGroupEntity orderGroup;

	@Column(name = "note")
	private String note;

	@Column(name = "status", nullable = false)
	private Byte status;

	@OneToMany
	@JoinColumn(name = "order_id")
	private Set<OrderDetailEntity> orderDetails = new LinkedHashSet<>();

	@OneToMany
	@JoinColumn(name = "order_id")
	private Set<OrderHistoryEntity> orderHistories = new LinkedHashSet<>();

	@Column(name = "order_group_id")
	private Long orderGroupId;

	@Column(name = "shipping_fee", nullable = false)
	private BigDecimal shippingFee;

	@Column(name = "shipping_label")
	private String shippingLabel;

}