package com.artiexh.model.rest.order.user.response;

import com.artiexh.model.rest.campaign.response.CampaignResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminCampaignOrderResponsePage extends CampaignOrderResponsePage {
	private CampaignResponse.Owner user;
}
