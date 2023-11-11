package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.CampaignSaleEntity;
import com.artiexh.model.rest.marketplace.response.SaleCampaignDetailResponse;
import com.artiexh.model.rest.marketplace.response.SaleCampaignResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {ProductMapper.class, CampaignTypeMapper.class}
)
public interface CampaignSaleMapper {

	SaleCampaignResponse entityToDomain(CampaignSaleEntity entity);

	SaleCampaignDetailResponse entityToDetailDomain(CampaignSaleEntity entity);

}
