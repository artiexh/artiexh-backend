package com.artiexh.data.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.LinkedHashSet;
import java.util.Set;

@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user")
public class UserEntity extends AccountEntity {

	@Size(max = 21)
	@Column(name = "google_id", length = 21)
	private String googleId;

	@Size(max = 20)
	@Column(name = "facebook_id", length = 20)
	private String facebookId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id")
	private CartEntity cart;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private Set<SubscriptionEntity> subscriptionsTo = new LinkedHashSet<>();

}