package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.StaffEntity;
import com.artiexh.data.jpa.entity.UserEntity;
import com.artiexh.model.domain.Account;
import com.artiexh.model.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {AccountMapper.class, ProductMapper.class, ProductAttachMapper.class, PasswordMapper.class}
)
public interface StaffMapper {
	StaffEntity domainToEntity(Account user);
	Account basicStaffInfo(StaffEntity staff);
}
