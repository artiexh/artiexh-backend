package com.artiexh.data.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@IdClass(CustomProductTagId.class)
@Table(name = "custom_product_tag")
public class CustomProductTagEntity {

	@Id
	@Column(name = "custom_product_id", nullable = false)
	private Long customProductId;

	@Id
	@Column(name = "name", nullable = false)
	private String name;

}