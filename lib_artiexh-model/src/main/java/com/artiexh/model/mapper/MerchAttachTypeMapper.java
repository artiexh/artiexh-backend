package com.artiexh.model.mapper;

import com.artiexh.model.domain.MerchAttachType;
import org.mapstruct.Mapper;

@Mapper
public interface MerchAttachTypeMapper {

    default Integer toValue(MerchAttachType type) {
        return type.getValue();
    }

    default MerchAttachType toMerchAttachType(Integer value) {
        return MerchAttachType.fromValue(value);
    }

}
