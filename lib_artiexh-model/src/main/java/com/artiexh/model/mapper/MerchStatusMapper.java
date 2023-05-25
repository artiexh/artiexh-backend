package com.artiexh.model.mapper;

import com.artiexh.model.domain.MerchStatus;
import org.mapstruct.Mapper;

@Mapper
public interface MerchStatusMapper {

	default Integer toValue(MerchStatus status) {
		return status.getValue();
	}

	default MerchStatus toMerchStatus(Integer value) {
		return MerchStatus.fromValue(value);
	}

}
