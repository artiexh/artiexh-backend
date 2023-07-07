package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "base_product")
public class BaseProductEntity {
	@Id
	@Tsid
	@Column(name = "id", nullable = false)
	public Long id;

	@Column(name = "name", nullable = false)
	public String name;

	@Column(name = "description", nullable = false)
	public String description;
}
