package com.artiexh.model.rest.campaign.response;

import com.artiexh.model.domain.CampaignHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CampaignDetailResponse extends CampaignResponse {
	private Set<CustomProductResponse> customProducts;
	private Set<CampaignHistory> campaignHistories;
}