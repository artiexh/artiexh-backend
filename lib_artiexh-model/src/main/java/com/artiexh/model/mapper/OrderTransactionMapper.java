package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.OrderTransactionEntity;
import com.artiexh.model.domain.OrderTransaction;
import com.artiexh.model.domain.ResponseCode;
import com.artiexh.model.rest.transaction.OrderTransactionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderTransactionMapper {
	@Mapping(target = "message", source = "responseCode", qualifiedByName = "toMessage")
	OrderTransactionResponse domainToResponse(OrderTransaction domain);

	OrderTransaction entityToDomain(OrderTransactionEntity entity);

	@Named("toMessage")
	default String toMessage(String responseCode) {
		return ResponseCode.fromCode(responseCode).getMessage();
	}
}
