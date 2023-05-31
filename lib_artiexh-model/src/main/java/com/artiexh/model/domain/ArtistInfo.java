package com.artiexh.model.domain;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ArtistInfo {
	public String name;
	private String avatarUrl;
}
