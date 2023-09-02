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
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {UserAddressMapper.class, UserMapper.class, ProductMapper.class, ShopMapper.class, AddressMapper.class})
public interface OrderMapper {

	@Mapping(target = "user", qualifiedByName = "entityToBasicUser")
	Order entityToResponseDomain(OrderEntity entity);

	OrderEntity orderToOrderEntity(Order order);

	ShopOrderResponse orderToArtistResponse(Order order);

	UserOrderResponse orderToUserResponse(Order order);

	ShopOrderResponsePage orderToArtistResponsePage(Order order);

	UserOrderResponsePage domainToUserResponsePage(Order order);


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
