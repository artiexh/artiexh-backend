package com.artiexh.model.mapper;

import com.artiexh.model.domain.DeliveryType;
import org.mapstruct.Mapper;

@Mapper
public interface DeliveryTypeMapper {

	default Integer toValue(DeliveryType type) {
		return type.getValue();
	}

	default DeliveryType toDeliveryType(Integer value) {
		return DeliveryType.fromValue(value);
	}

}
