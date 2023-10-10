package com.artiexh.model.domain;

import com.artiexh.model.rest.account.AccountProfile;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostComment {
	private Long id;

	private String content;

	private Post post;

	private User owner;
}
