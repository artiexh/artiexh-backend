package com.artiexh.data.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_detail")
@IdClass(OrderDetailEntityId.class)
public class OrderDetailEntity {

	@Id
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "campaign_order_id", nullable = false)
	private CampaignOrderEntity campaignOrder;

	@Id
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "product_id", nullable = false)
	private ProductEntity product;

	@NotNull
	@Column(name = "quantity", nullable = false)
	private Integer quantity;

}