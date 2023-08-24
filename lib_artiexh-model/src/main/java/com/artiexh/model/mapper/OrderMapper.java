package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.OrderEntity;
import com.artiexh.model.domain.Order;
import com.artiexh.model.domain.OrderStatus;
import com.artiexh.model.rest.artist.ShopOrderResponsePage;
import com.artiexh.model.rest.user.UserOrderResponsePage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {UserAddressMapper.class, UserMapper.class, ProductMapper.class, ShopMapper.class})
public interface OrderMapper {
	Order entityToDomain(OrderEntity entity);

	ShopOrderResponsePage domainToArtistResponsePage(Order order);

	@Mapping(source = "shop", target = "shop")
	UserOrderResponsePage domainToUserResponsePage(Order order);

	default Integer toValue(OrderStatus status) {
		return status.getValue();
	}

	default OrderStatus toOrderStatus(Integer value) {
		return OrderStatus.fromValue(value);
	}
}
