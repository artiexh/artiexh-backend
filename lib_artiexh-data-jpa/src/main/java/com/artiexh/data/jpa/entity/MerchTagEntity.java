package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;
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
	@Tsid
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "name", nullable = false, unique = true)
	private String name;

	@ManyToMany
	@JoinTable(name = "merch_tag_mapping",
		joinColumns = @JoinColumn(name = "tag_id"),
		inverseJoinColumns = @JoinColumn(name = "merch_id"))
	private Set<MerchEntity> merches;

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (!(obj instanceof MerchTagEntity)) return false;
		MerchTagEntity o = (MerchTagEntity) obj;
		return o.getName().equals(this.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name.hashCode());
	}
}