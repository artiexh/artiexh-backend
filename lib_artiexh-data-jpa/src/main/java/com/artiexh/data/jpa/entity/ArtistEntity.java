package com.artiexh.data.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.LinkedHashSet;
import java.util.Set;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "artist")
public class ArtistEntity extends UserEntity {

	@MapsId
	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "id", nullable = false)
	private UserEntity user;

	@OneToMany(mappedBy = "owner")
	private final Set<MerchEntity> merch = new LinkedHashSet<>();

	@OneToMany(mappedBy = "artist")
	private final Set<SubscriptionEntity> subscriptionsFrom = new LinkedHashSet<>();

}