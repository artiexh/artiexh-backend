package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.CartItemEntity;
import com.artiexh.model.domain.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {MerchMapper.class}
)
public interface CartItemMapper {

	CartItem entityToDomain(CartItemEntity cartItemEntity);

	CartItemEntity domainToEntity(CartItem cartItem);

}
