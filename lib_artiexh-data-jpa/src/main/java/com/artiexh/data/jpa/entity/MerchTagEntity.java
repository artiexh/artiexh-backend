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
@Table(name = "merch_tag")
public class MerchTagEntity {
	@ManyToMany
	@JoinTable(name = "merch_tag_mapping",
		joinColumns = @JoinColumn(name = "tag_id"),
		inverseJoinColumns = @JoinColumn(name = "merch_id"))
	private final Set<MerchEntity> merches = new LinkedHashSet<>();

	@Column(name = "name", nullable = false)
	private String name;
	@Id
	@GenericGenerator(name = "tsid", type = TsidGenerator.class)
	@Column(name = "id", nullable = false)
	private Long id;

}