package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.OrderEntity;
import com.artiexh.model.rest.order.user.response.DetailUserOrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {
		UserMapper.class,
		CampaignOrderMapper.class,
		OrderTransactionMapper.class,
		ProductMapper.class
	})
public interface OrderMapper {

//	@Mapping(target = "user", qualifiedByName = "entityToBasicUser")
//	@Mapping(target = "campaignOrders", source = "campaignOrders", qualifiedByName = "entitiesToDomainsWithoutOrder")
//	@Mapping(target = "currentTransaction", source = "orderTransactions", qualifiedByName = "getCurrentTransaction")
//	Order entityToDomain(OrderEntity entity);

//	@Named("domainToResponse")
//	UserOrderResponse domainToUserResponse(Order order);
//
//	@Named("domainToAdminResponse")
//	AdminOrderResponse domainToAdminResponse(Order order);
//
//	@Named("entityToResponse")
//	UserOrderResponse entityToUserResponse(OrderEntity order);
//
//	@Mapping(target = "currentTransaction", source = "orderTransactions", qualifiedByName = "getCurrentTransaction")
//	@Named("entityToAdminResponse")
//	AdminOrderResponse entityToAdminResponse(OrderEntity order);

	@Named("domainToDetailResponse")
	@Mapping(target = "campaignOrders", source = "campaignOrders", qualifiedByName = "entitiesToUserResponses")
	DetailUserOrderResponse entityToUserDetailResponse(OrderEntity entity);
}
