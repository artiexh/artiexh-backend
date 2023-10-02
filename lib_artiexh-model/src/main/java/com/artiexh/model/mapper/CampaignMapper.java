package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.CampaignEntity;
import com.artiexh.model.domain.CampaignStatus;
import com.artiexh.model.rest.campaign.response.CreateCampaignResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {CustomProductMapper.class}
)
public interface CampaignMapper {

	CreateCampaignResponse entityToResponse(CampaignEntity entity);

	default CampaignStatus campaignStatusFrom(byte value) {
		return CampaignStatus.fromValue(value);
	}

	default byte campaignStatusTo(CampaignStatus status) {
		return (byte) status.getValue();
	}
}
