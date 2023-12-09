package com.artiexh.model.rest.notification;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
	private String title;
	private String content;
}
