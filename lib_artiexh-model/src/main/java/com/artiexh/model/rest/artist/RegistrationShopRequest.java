package com.artiexh.model.rest.artist;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationShopRequest {

	@NotEmpty
	private String shopName;

	private String shopImageUrl;

	@NotNull
	private Integer shopWardId;

	@NotEmpty
	private String shopAddress;

	@NotEmpty
	private String shopPhone;
}