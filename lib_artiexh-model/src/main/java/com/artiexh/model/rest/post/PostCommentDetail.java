package com.artiexh.model.rest.post;

import com.artiexh.model.domain.Post;
import com.artiexh.model.rest.account.AccountProfile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostCommentDetail {
	@JsonSerialize(using = ToStringSerializer.class)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;

	private String content;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(allOf = {AccountProfile.class})
	private AccountProfile owner;
}
