package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.OrderEntity;
import com.artiexh.model.domain.Order;
import com.artiexh.model.domain.OrderDetail;
import com.artiexh.model.domain.OrderStatus;
import com.artiexh.model.rest.artist.ShopOrderResponse;
import com.artiexh.model.rest.artist.ShopOrderResponsePage;
import com.artiexh.model.rest.order.response.OrderDetailResponse;
import com.artiexh.model.rest.user.UserOrderResponse;
import com.artiexh.model.rest.user.UserOrderResponsePage;
import org.mapstruct.*;

import java.util.Set;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {
		UserAddressMapper.class,
		UserMapper.class,
		ProductMapper.class,
		ShopMapper.class,
		OrderTransactionMapper.class,
		AddressMapper.class})
public interface OrderMapper {

	//@Mapping(target = "user", qualifiedByName = "entityToBasicUser")
	Order entityToResponseDomain(OrderEntity entity);

	OrderEntity orderToOrderEntity(Order order);

	ShopOrderResponse domainToArtistResponse(Order order);

	@Named("domainToUserResponse")
	UserOrderResponse domainToUserResponse(Order order);

	@IterableMapping(qualifiedByName = "domainToUserResponse")
	@Named("domainsToUserResponses")
	Set<UserOrderResponse> domainsToUserResponses(Set<Order> orders);

	ShopOrderResponsePage domainToArtistResponsePage(Order order);

	@Named("domainToUserResponsePage")
	UserOrderResponsePage domainToUserResponsePage(Order order);

	@IterableMapping(qualifiedByName = "domainToUserResponsePage")
	@Named("domainsToUserResponsePages")
	Set<UserOrderResponsePage> domainsToUserResponsePages(Set<Order> orders);


	@Mapping(target = "id", source = "product.id")
	@Mapping(target = "status", source = "product.status")
	@Mapping(target = "price.unit", source = "product.price.unit")
	@Mapping(target = "name", source = "product.name")
	@Mapping(target = "thumbnailUrl", source = "product.thumbnailUrl")
	@Mapping(target = "price.amount", source = "product.price.amount")
	@Mapping(target = "description", source = "product.description")
	@Mapping(target = "type", source = "product.type")
	@Mapping(target = "remainingQuantity", source = "product.remainingQuantity")
	@Mapping(target = "publishDatetime", source = "product.publishDatetime")
	@Mapping(target = "maxItemsPerOrder", source = "product.maxItemsPerOrder")
	@Mapping(target = "deliveryType", source = "product.deliveryType")
	OrderDetailResponse domainToOrderDetailResponse(OrderDetail order);

	default Integer toValue(OrderStatus status) {
		return status.getValue();
	}

	default OrderStatus toOrderStatus(Integer value) {
		return OrderStatus.fromValue(value);
	}
}
