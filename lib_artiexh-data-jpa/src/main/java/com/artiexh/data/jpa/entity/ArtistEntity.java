package com.artiexh.data.jpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.LinkedHashSet;
import java.util.Set;

@SuperBuilder
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "artist")
public class ArtistEntity extends UserEntity {

	@OneToMany(mappedBy = "owner")
	private final Set<MerchEntity> merch = new LinkedHashSet<>();

	@OneToMany(mappedBy = "artist")
	private final Set<SubscriptionEntity> subscriptionsFrom = new LinkedHashSet<>();

}