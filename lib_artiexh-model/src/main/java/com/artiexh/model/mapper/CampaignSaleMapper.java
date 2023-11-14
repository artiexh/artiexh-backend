package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.CampaignSaleEntity;
import com.artiexh.model.rest.marketplace.salecampaign.response.SaleCampaignDetailResponse;
import com.artiexh.model.rest.marketplace.salecampaign.response.SaleCampaignResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {ProductMapper.class, CampaignTypeMapper.class}
)
public interface CampaignSaleMapper {

	@Named("entityToResponse")
	SaleCampaignResponse entityToResponse(CampaignSaleEntity entity);

	@Named("entityToDetailResponse")
	@Mapping(target = "products", source = "products", qualifiedByName = "entityToProductInSaleResponse")
	SaleCampaignDetailResponse entityToDetailResponse(CampaignSaleEntity entity);

}
