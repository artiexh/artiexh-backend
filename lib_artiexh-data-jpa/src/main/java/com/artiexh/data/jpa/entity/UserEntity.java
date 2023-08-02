package com.artiexh.data.jpa.entity;

import jakarta.persistence.*;
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

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private final Set<SubscriptionEntity> subscriptionsTo = new LinkedHashSet<>();
	@Column(name = "twitter_id", length = 20)
	private String twitterId;
	@Column(name = "facebook_id", length = 20)
	private String facebookId;
	@Column(name = "google_id", length = 21)
	private String googleId;
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id")
	private CartEntity shoppingCart;

}