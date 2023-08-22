package com.artiexh.data.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.LinkedHashSet;
import java.util.Set;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "artist")
public class ArtistEntity extends UserEntity {

	@ManyToOne
	@JoinColumn(name = "province_id")
	private ProvinceEntity province;

	@Size(max = 255)
	@NotNull
	@Column(name = "shop_name", nullable = false)
	private String shopName;

	@OneToMany(mappedBy = "owner")
	private Set<ProductEntity> ownProducts = new LinkedHashSet<>();

	@OneToMany(mappedBy = "shop")
	private Set<ProductEntity> shopProducts = new LinkedHashSet<>();

	@OneToMany(mappedBy = "artist")
	private Set<SubscriptionEntity> subscriptionsFrom = new LinkedHashSet<>();

}