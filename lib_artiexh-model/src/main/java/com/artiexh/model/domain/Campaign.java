package com.artiexh.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {

	private Long id;

	private CampaignStatus status;

	private Set<CampaignHistory> campaignHistories;

}
