package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@IdClass(CustomProductTagId.class)
@Table(name = "custom_product_tag")
public class CustomProductTag {

	@Id
	@Tsid
	private Long customProductId;

	@Id
	private String name;

}