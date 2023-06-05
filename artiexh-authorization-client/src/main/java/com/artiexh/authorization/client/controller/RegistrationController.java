package com.artiexh.authorization.client.controller;

import com.artiexh.api.base.common.Endpoint;
import com.artiexh.authorization.client.authentication.ResponseTokenProcessor;
import com.artiexh.authorization.client.service.RegistrationService;
import com.artiexh.model.domain.Artist;
import com.artiexh.model.domain.PrinterProvider;
import com.artiexh.model.domain.User;
import com.artiexh.model.mapper.PrinterProviderMapper;
import com.artiexh.model.mapper.UserMapper;
import com.artiexh.model.request.RegisterPrinterProviderRequest;
import com.artiexh.model.request.RegisterUserRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(Endpoint.Registration.ROOT)
@RequiredArgsConstructor
public class RegistrationController {

	private final UserMapper userMapper;
	private final PrinterProviderMapper printerProviderMapper;
	private final RegistrationService registrationService;
	private final ResponseTokenProcessor responseTokenProcessor;

	@PostMapping(Endpoint.Registration.USER)
	public User registerUser(HttpServletResponse response, @RequestBody @Valid RegisterUserRequest registerUserRequest) {
		try {
			User user = registrationService.createUser(userMapper.registerUserRequestToDomain(registerUserRequest));
			responseTokenProcessor.process(response, user.getId().toString(), user.getRole());
			return user;
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
		}
	}

	@PostMapping(Endpoint.Registration.PRINTER_PROVIDER)
	@PreAuthorize("hasAuthority('ADMIN')")
	public PrinterProvider registerPrinterProvider(@RequestBody @Valid RegisterPrinterProviderRequest request) {
		return registrationService.createPrinterProvider(
			printerProviderMapper.registerPrinterProviderRequestToDomain(request)
		);
	}

	@PostMapping(Endpoint.Registration.ARTIST)
	@PreAuthorize("hasAuthority('USER')")
	public Artist registerArtist(Authentication authentication) {
		Long id = (Long) authentication.getPrincipal();
		try {
			return registrationService.registerArtist(id);
		} catch (IllegalArgumentException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
		}
	}

}
