package com.artiexh.model.rest.artist.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationArtistRequest {

	@NotEmpty
	private String bankAccount;

	@NotEmpty
	private String bankName;

	@NotNull
	private Integer wardId;

	@NotEmpty
	private String address;

	@NotEmpty
	private String phone;
}