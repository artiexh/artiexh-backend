package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.OrderTransactionEntity;
import com.artiexh.model.domain.OrderTransaction;
import com.artiexh.model.rest.transaction.OrderTransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderTransactionMapper {
	OrderTransactionResponse domainToResponse(OrderTransaction domain);
	OrderTransaction entityToDomain(OrderTransactionEntity entity);
}
