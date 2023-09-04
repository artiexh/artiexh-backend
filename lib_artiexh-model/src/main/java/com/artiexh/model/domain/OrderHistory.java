package com.artiexh.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistory {

	private Long oderId;

	private OrderHistory status;

	private LocalDateTime datetime;

}
