package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.OrderGroupEntity;
import com.artiexh.model.domain.OrderGroup;
import com.artiexh.model.rest.user.UserOrderGroupResponse;
import com.artiexh.model.rest.user.UserOrderGroupResponsePage;
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

	@Mapping(target = "orders", qualifiedByName = "domainsToUserResponsePages")
	UserOrderGroupResponsePage domainToUserResponsePage(OrderGroup orderGroup);

	@Mapping(target = "orders", qualifiedByName = "domainsToUserResponses")
	UserOrderGroupResponse domainToUserResponse(OrderGroup orderGroup);
}
