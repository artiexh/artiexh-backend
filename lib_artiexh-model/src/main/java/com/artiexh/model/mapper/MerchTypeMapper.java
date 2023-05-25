package com.artiexh.model.mapper;

import com.artiexh.model.domain.MerchType;
import org.mapstruct.Mapper;

@Mapper
public interface MerchTypeMapper {

	default Integer toValue(MerchType type) {
		return type.getValue();
	}

	default MerchType toMerchType(Integer value) {
		return MerchType.fromValue(value);
	}

}
