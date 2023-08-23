package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.UserAddressEntity;
import com.artiexh.model.domain.UserAddress;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserAddressMapper {

	UserAddressEntity domainToEntity(UserAddress userAddress);

	UserAddress entityToDomain(UserAddressEntity userAddressEntity);

	default Byte userAddressTypeToByte(UserAddress.Type type) {
		return (byte) type.getValue();
	}

	default UserAddress.Type byteToUserAddressType(Byte type) {
		return UserAddress.Type.from(type);
	}
}
