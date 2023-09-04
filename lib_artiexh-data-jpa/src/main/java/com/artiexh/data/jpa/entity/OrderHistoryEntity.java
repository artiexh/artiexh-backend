package com.artiexh.data.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "order_history")
public class OrderHistoryEntity {

	@EmbeddedId
	private OrderHistoryId id;

	@MapsId("status")
	private Byte status;

	@NotNull
	@Column(name = "datetime", nullable = false)
	private Instant datetime;

}