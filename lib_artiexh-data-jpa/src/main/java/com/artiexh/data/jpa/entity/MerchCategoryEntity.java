package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.TsidGenerator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.LinkedHashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "merch_category")
public class MerchCategoryEntity {
	@ManyToMany
	@JoinTable(name = "merch_category_mapping",
		joinColumns = @JoinColumn(name = "category_id"),
		inverseJoinColumns = @JoinColumn(name = "merch_id"))
	private final Set<MerchEntity> merch = new LinkedHashSet<>();

	@Column(name = "name", nullable = false)
	private String name;
	@Id
	@GenericGenerator(name = "tsid", type = TsidGenerator.class)
	@Column(name = "id", nullable = false)
	private Long id;

}