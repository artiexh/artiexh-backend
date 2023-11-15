package com.artiexh.model.rest.artist.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

	private Integer wardId;

	private String address;

	@NotEmpty
	private String phone;

	private String shopThumbnailUrl;

	@Size(max = 255)
	private String description;
}
