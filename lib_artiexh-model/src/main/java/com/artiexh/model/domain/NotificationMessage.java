package com.artiexh.model.domain;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessage {
	private String content;
	private String title;
	private Long ownerId;
	private Long id;
}
