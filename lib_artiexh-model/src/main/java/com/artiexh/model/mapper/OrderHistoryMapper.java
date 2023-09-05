package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.OrderHistoryEntity;
import com.artiexh.model.domain.OrderHistory;
import com.artiexh.model.domain.OrderHistoryStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderHistoryMapper {

	@Mapping(target = "status", source = "id.status")
	OrderHistory entityToOrderHistory(OrderHistoryEntity entity);

	default OrderHistoryStatus fromValue(Integer value) {
		return OrderHistoryStatus.from(value);
	}
}
