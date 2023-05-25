package com.artiexh.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {
	private User user;
	private Artist artist;
	private LocalDateTime expiredDatetime;
}
