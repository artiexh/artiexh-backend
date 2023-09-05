package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.OrderGroupEntity;
import com.artiexh.model.domain.OrderGroup;
import com.artiexh.model.rest.order.response.UserOrderGroupResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {
		UserAddressMapper.class,
		UserMapper.class,
		ProductMapper.class,
		ShopMapper.class,
		OrderTransactionMapper.class,
		OrderMapper.class,
		AddressMapper.class})
public interface OrderGroupMapper {
	@Mapping(target = "user", qualifiedByName = "entityToBasicUser")
	OrderGroup entityToDomain(OrderGroupEntity entity);
	UserOrderGroupResponse domainToUserResponse(OrderGroup orderGroup);
}
