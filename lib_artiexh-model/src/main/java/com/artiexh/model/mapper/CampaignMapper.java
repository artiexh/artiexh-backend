package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.CampaignEntity;
import com.artiexh.data.jpa.entity.CampaignHistoryEntity;
import com.artiexh.model.domain.CampaignHistory;
import com.artiexh.model.domain.CampaignHistoryAction;
import com.artiexh.model.domain.CampaignStatus;
import com.artiexh.model.rest.campaign.response.CampaignDetailResponse;
import com.artiexh.model.rest.campaign.response.CampaignResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {CustomProductMapper.class}
)
public interface CampaignMapper {

	CampaignResponse entityToResponse(CampaignEntity entity);

	@Mapping(target = "provider", ignore = true)
	CampaignDetailResponse entityToDetailResponse(CampaignEntity entity);

	@Mapping(target = "eventTime", source = "id.eventTime")
	@Mapping(target = "campaignId", source = "id.campaignId")
	CampaignHistory campaignHistoryEntityToCampaignHistory(CampaignHistoryEntity entity);

	default CampaignStatus campaignStatusFrom(byte value) {
		return CampaignStatus.fromValue(value);
	}

	default byte campaignStatusTo(CampaignStatus status) {
		return (byte) status.getValue();
	}

	default CampaignHistoryAction campaignHistoryActionFrom(byte value) {
		return CampaignHistoryAction.fromValue(value);
	}

	default byte campaignHistoryActionTo(CampaignHistoryAction status) {
		return (byte) status.getValue();
	}
}
