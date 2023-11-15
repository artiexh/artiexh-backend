package com.artiexh.model.rest.artist.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.Length;

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

	@NotEmpty
	private String phone;

	private String shopThumbnailUrl;

	@Size(max = 255)
	private String description;
}