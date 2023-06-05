package com.artiexh.model.domain;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ArtistInfo {
	private String displayName;
	private String avatarUrl;
}
