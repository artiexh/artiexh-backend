package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.LinkedHashSet;
import java.util.Set;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
public class UserEntity {
	@Id
	@Tsid
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "username", nullable = false)
	private String username;

	@Column(name = "password", nullable = false, length = 60)
	private String password;

	@Column(name = "role", nullable = false)
	private Byte role;

	@Column(name = "status", nullable = false)
	private Byte status;

	@Column(name = "avatar_url", length = 2048)
	private String avatarUrl;

	@Column(name = "email", length = 254)
	private String email;

	@Column(name = "twitter_id", length = 20)
	private String twitterId;

	@Column(name = "facebook_id", length = 20)
	private String facebookId;

	@Column(name = "google_id", length = 21)
	private String googleId;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private Set<SubscriptionEntity> subscriptionsTo = new LinkedHashSet<>();

}