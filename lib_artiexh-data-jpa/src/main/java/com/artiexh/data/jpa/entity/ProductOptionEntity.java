package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "product_option")
public class ProductOptionEntity {
	@Id
	@Tsid
	private Long id;

	@Column(name = "product_id")
	private Long productId;

	@Column(name = "name", length = 50)
	private String name;

	@Column(name = "`index`")
	private Integer index;

	@Column(name = "is_optional")
	private Boolean isOptional;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "option_id")
	private Set<OptionValueEntity> optionValues;
}
