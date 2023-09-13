//package com.artiexh.data.jpa.entity;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Table;
//import lombok.Data;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import lombok.experimental.SuperBuilder;
//
//import java.time.Instant;
//
//@SuperBuilder(toBuilder = true)
//@Data
//@NoArgsConstructor
//@Getter
//@Setter
//@Entity
//@Table(name = "pre_order_product")
//public class PreOrderProductEntity extends ProductEntity {
//
//	@Column(name = "start_datetime", nullable = false)
//	private Instant startDatetime;
//
//	@Column(name = "end_datetime", nullable = false)
//	private Instant endDatetime;
//
//	public PreOrderProductEntity(ProductEntity instance) {
//		super(instance.toBuilder());
//	}
//
//}