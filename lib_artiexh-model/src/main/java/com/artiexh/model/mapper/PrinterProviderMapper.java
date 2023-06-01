package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.PrinterProviderEntity;
import com.artiexh.model.domain.PrinterProvider;
import com.artiexh.model.request.RegisterPrinterProviderRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {RoleMapper.class, UserStatusMapper.class}
)
public abstract class PrinterProviderMapper {

	@Autowired
	protected PasswordEncoder passwordEncoder;

	public abstract PrinterProvider entityToDomain(PrinterProviderEntity printerProviderEntity);

	public abstract PrinterProviderEntity domainToEntity(PrinterProvider printerProvider);

	@Mapping(target = "role", constant = "PRINTER_PROVIDER")
	@Mapping(target = "status", constant = "ACTIVE")
	@Mapping(target = "password", expression = "java(passwordEncoder.encode(request.getPassword()))")
	public abstract PrinterProvider registerPrinterProviderRequestToDomain(RegisterPrinterProviderRequest request);

}
