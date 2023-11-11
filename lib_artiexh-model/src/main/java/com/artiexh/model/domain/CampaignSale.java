package com.artiexh.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignSale {
	private Long id;
	private String name;
	private String description;
	private Instant publicDate;
	private Instant orderFrom;
	private Instant orderTo;
	private Long createdBy;
	private Set<Product> products;
	private Artist owner;
	private String content;
	private String thumbnailUrl;
	private CampaignType campaignType;
}
