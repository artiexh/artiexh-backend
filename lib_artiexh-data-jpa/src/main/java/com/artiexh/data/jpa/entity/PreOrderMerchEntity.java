package com.artiexh.data.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@SuperBuilder(toBuilder = true)
@Data
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

	public PreOrderMerchEntity(MerchEntity instance) {
		super(instance.toBuilder());
	}

	public static PreOrderMerchEntityBuilder<?, ?> parentBuilder(MerchEntity instance) {
		return new PreOrderMerchEntity(instance).toBuilder();
	}

}