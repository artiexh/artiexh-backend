package com.artiexh.model.mapper;

import com.artiexh.model.domain.UserStatus;
import org.mapstruct.Mapper;

@Mapper
public interface UserStatusMapper {

    default Integer toValue(UserStatus status) {
        return status.getValue();
    }

    default UserStatus toUserStatus(Integer value) {
        return UserStatus.fromValue(value);
    }

}
