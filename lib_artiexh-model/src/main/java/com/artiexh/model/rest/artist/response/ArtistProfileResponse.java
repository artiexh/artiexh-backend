package com.artiexh.model.rest.artist.response;

import com.artiexh.model.domain.Subscription;
import com.artiexh.model.domain.Ward;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistProfileResponse {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	private String username;

	private String displayName;

	private String email;

	private String avatarUrl;

	private long numOfSubscriptions;

	private Set<Subscription> subscriptionsFrom;

	private Ward shopWard;

	private String shopAddress;

	private String shopPhone;
}
