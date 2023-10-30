package com.artiexh.data.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(ProductInCampaignTagId.class)
@Table(name = "product_in_campaign_tag")
public class ProductInCampaignTagEntity {

	@Id
	@Column(name = "product_in_campaign_id", nullable = false)
	private Long productInCampaignId;

	@Id
	@Column(name = "name", nullable = false)
	private String name;

}