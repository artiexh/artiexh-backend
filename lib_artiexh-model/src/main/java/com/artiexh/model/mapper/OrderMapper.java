package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.OrderEntity;
import com.artiexh.model.domain.Order;
import com.artiexh.model.rest.order.user.response.UserOrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {
		UserAddressMapper.class,
		UserMapper.class,
		ProductMapper.class,
		CampaignMapper.class,
		OrderTransactionMapper.class,
		CampaignOrderMapper.class,
		AddressMapper.class})
public interface OrderMapper {
	@Mapping(target = "user", qualifiedByName = "entityToBasicUser")
	Order entityToDomain(OrderEntity entity);

	@Mapping(target = "campaignOrders", source = "campaignOrders", qualifiedByName = "domainsToUserResponses")
	UserOrderResponse domainToUserResponse(Order order);
}
