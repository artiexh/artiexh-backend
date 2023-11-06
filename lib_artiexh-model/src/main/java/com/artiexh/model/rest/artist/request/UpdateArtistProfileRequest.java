package com.artiexh.model.rest.artist.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateArtistProfileRequest {
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
