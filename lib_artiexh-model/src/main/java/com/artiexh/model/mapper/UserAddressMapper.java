package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.UserAddressEntity;
import com.artiexh.model.domain.UserAddress;
import com.artiexh.model.rest.user.UserAddressRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserAddressMapper {

	UserAddressEntity domainToEntity(UserAddress userAddress);


	@Mapping(target = "ward.id", source = "wardId")
	UserAddressEntity userAddressRequestToEntity(UserAddressRequest userAddressRequest);

	@Mapping(target = "ward.district.wards", ignore = true)
	@Mapping(target = "ward.district.province.districts", ignore = true)
	UserAddress entityToDomain(UserAddressEntity userAddressEntity);

	default Byte userAddressTypeToByte(UserAddress.Type type) {
		return (byte) type.getValue();
	}

	default UserAddress.Type byteToUserAddressType(Byte type) {
		return UserAddress.Type.from(type);
	}

}
