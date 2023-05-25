package com.artiexh.data.jpa.entity;

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
@Table(name = "merch_tag")
public class MerchTagEntity {
	@Id
	@GenericGenerator(name = "tsid", strategy = "io.hypersistence.utils.hibernate.id.TsidGenerator")
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@ManyToMany
	@JoinTable(name = "merch_tag_mapping",
		joinColumns = @JoinColumn(name = "tag_id"),
		inverseJoinColumns = @JoinColumn(name = "merch_id"))
	private Set<MerchEntity> merches = new LinkedHashSet<>();

}