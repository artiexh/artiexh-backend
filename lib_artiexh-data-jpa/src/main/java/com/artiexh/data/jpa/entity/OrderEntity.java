package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.*;
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

	@Builder.Default
	@OneToMany
	@JoinColumn(name = "order_id")
	private Set<OrderDetailEntity> orderDetails = new LinkedHashSet<>();

	@Builder.Default
	@OneToMany
	@JoinColumn(name = "order_id")
	private Set<OrderHistoryEntity> orderHistories = new LinkedHashSet<>();

	@Column(name = "order_group_id")
	private Long orderGroupId;

	@Column(name = "shipping_fee", nullable = false)
	private BigDecimal shippingFee;

	@Column(name = "shipping_label")
	private String shippingLabel;

	@Column(name = "pick_address")
	private String pickAddress;

	@Column(name = "pick_ward")
	private String pickWard;

	@Column(name = "pick_district")
	private String pickDistrict;

	@Column(name = "pick_province")
	private String pickProvince;

	@Column(name = "pick_country")
	private String pickCountry;

	@Column(name = "pick_tel")
	private String pickTel;

	@Column(name = "pick_name")
	private String pickName;

	@Column(name = "pick_email", length = 254)
	private String pickEmail;

	@Column(name = "return_address")
	private String returnAddress;

	@Column(name = "return_ward")
	private String returnWard;

	@Column(name = "return_district")
	private String returnDistrict;

	@Column(name = "return_province")
	private String returnProvince;

	@Column(name = "return_country")
	private String returnCountry;

	@Column(name = "return_tel", length = 15)
	private String returnTel;

	@Column(name = "return_name")
	private String returnName;

	@Column(name = "return_email")
	private String returnEmail;

}