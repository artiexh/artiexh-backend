package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.CampaignOrderEntity;
import com.artiexh.data.jpa.entity.OrderDetailEntity;
import com.artiexh.model.domain.CampaignOrder;
import com.artiexh.model.domain.OrderDetail;
import com.artiexh.model.domain.OrderStatus;
import com.artiexh.model.rest.order.admin.response.AdminCampaignOrderResponse;
import com.artiexh.model.rest.order.response.OrderDetailResponse;
import com.artiexh.model.rest.order.user.response.UserCampaignOrderResponse;
import com.artiexh.model.rest.order.user.response.UserCampaignOrderResponsePage;
import org.mapstruct.*;

import java.util.Set;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {
		UserAddressMapper.class,
		UserMapper.class,
		ProductMapper.class,
		CampaignMapper.class,
		OrderTransactionMapper.class,
		AddressMapper.class,
		OrderHistoryMapper.class,
		DateTimeMapper.class
	}
)
public interface CampaignOrderMapper {

	@Mapping(target = "orderId", source = "order.id")
	CampaignOrder entityToResponseDomain(CampaignOrderEntity entity);

	@Mapping(target = "createdDate", ignore = true)
	@Mapping(target = "modifiedDate", ignore = true)
	CampaignOrderEntity orderToOrderEntity(CampaignOrder campaignOrder);

	AdminCampaignOrderResponse entityToUserResponse(CampaignOrderEntity entity);

	@Mapping(target = "currentTransaction", ignore = true)
	@Named("domainToUserResponse")
	UserCampaignOrderResponse domainToUserResponse(CampaignOrder campaignOrder);

	@IterableMapping(qualifiedByName = "domainToUserResponse")
	@Named("domainsToUserResponses")
	Set<UserCampaignOrderResponse> domainsToUserResponses(Set<CampaignOrder> campaignOrders);


	@Named("domainToUserResponsePage")
	UserCampaignOrderResponse domainToUserResponsePage(CampaignOrder campaignOrder);

	@IterableMapping(qualifiedByName = "domainToUserResponsePage")
	@Named("domainsToUserResponsePages")
	Set<UserCampaignOrderResponsePage> domainsToUserResponsePages(Set<CampaignOrder> campaignOrders);


	@Mapping(target = "id", source = "product.id")
	@Mapping(target = "status", source = "product.status")
	@Mapping(target = "price.unit", source = "product.price.unit")
	@Mapping(target = "name", source = "product.name")
	@Mapping(target = "thumbnailUrl", source = "product.thumbnailUrl")
	@Mapping(target = "price.amount", source = "product.price.amount")
	@Mapping(target = "description", source = "product.description")
	@Mapping(target = "type", source = "product.type")
	@Mapping(target = "remainingQuantity", expression = "java(order.getProduct().getQuantity() - order.getProduct().getSoldQuantity())")
	@Mapping(target = "maxItemsPerOrder", source = "product.maxItemsPerOrder")
	@Mapping(target = "deliveryType", source = "product.deliveryType")
	OrderDetailResponse domainToOrderDetailResponse(OrderDetail order);

	@Mapping(target = "id", source = "product.id")
	@Mapping(target = "status", source = "product.status")
	@Mapping(target = "price.unit", source = "product.priceUnit")
	@Mapping(target = "name", source = "product.name")
	@Mapping(target = "thumbnailUrl", source = "product.attaches", qualifiedByName = "getProductThumbnailUrl")
	@Mapping(target = "price.amount", source = "product.priceAmount")
	@Mapping(target = "description", source = "product.description")
	@Mapping(target = "type", source = "product.type")
	@Mapping(target = "remainingQuantity", expression = "java(entity.getProduct().getQuantity() - entity.getProduct().getSoldQuantity())")
	@Mapping(target = "maxItemsPerOrder", source = "product.maxItemsPerOrder")
	@Mapping(target = "deliveryType", source = "product.deliveryType")
	OrderDetailResponse entityToOrderDetailResponse(OrderDetailEntity entity);

	default Integer toValue(OrderStatus status) {
		return status.getValue();
	}

	default OrderStatus toOrderStatus(Integer value) {
		return OrderStatus.fromValue(value);
	}
}
