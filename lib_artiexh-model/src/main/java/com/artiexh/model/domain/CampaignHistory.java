package com.artiexh.model.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignHistory {
	@JsonIgnore
	private Long campaignId;
	private LocalDateTime eventTime;
	private CampaignHistoryAction action;
	private String message;
}
