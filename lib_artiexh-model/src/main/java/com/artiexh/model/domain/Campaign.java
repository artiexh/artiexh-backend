package com.artiexh.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Campaign {

	private Long id;

	private CampaignStatus status;

	private String name;

	private String description;

	private Boolean isPublished;

	private String thumbnailUrl;

	private String content;

	private CampaignType campaignType;

	private Set<CampaignHistory> campaignHistories;

}
