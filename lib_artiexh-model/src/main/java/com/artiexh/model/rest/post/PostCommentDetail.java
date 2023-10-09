package com.artiexh.model.rest.post;

import com.artiexh.model.domain.Post;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostCommentDetail {
	private Long id;

	private String content;

	private PostDetail post;
}
