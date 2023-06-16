package com.artiexh.data.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "artist")
public class ArtistEntity extends UserEntity {

	@OneToMany(mappedBy = "owner")
	private Set<MerchEntity> merch;

	@OneToMany(mappedBy = "artist")
	private Set<SubscriptionEntity> subscriptionsFrom;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "province_id")
	private ProvinceEntity province;

}