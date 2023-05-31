package com.artiexh.data.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "pre_order_merch")
public class PreOrderMerchEntity extends MerchEntity {

	@Column(name = "start_datetime", nullable = false)
	private Instant startDatetime;

	@Column(name = "end_datetime", nullable = false)
	private Instant endDatetime;

}