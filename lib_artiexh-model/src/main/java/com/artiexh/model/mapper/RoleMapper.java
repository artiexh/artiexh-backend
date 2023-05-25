package com.artiexh.model.mapper;

import com.artiexh.model.domain.Role;
import org.mapstruct.Mapper;

@Mapper
public interface RoleMapper {

	default int toValue(Role role) {
		return role.getValue();
	}

	default Role toRole(int value) {
		return Role.fromValue(value);
	}

}
