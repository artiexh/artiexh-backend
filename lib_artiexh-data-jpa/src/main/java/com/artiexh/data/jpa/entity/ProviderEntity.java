package com.artiexh.data.jpa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "provider")
public class ProviderEntity {
	@Id
	@Column(name = "business_code", nullable = false, length = 13)
	private String businessCode;

	@Column(name = "business_name", nullable = false)
	private String businessName;

	@Column(name = "address", nullable = false)
	private String address;

	@Column(name = "contact_name", nullable = false)
	private String contactName;

	@Column(name = "email", length = 254)
	private String email;

	@Column(name = "phone", length = 15, nullable = false)
	private String phone;

	@Column(name = "description", nullable = false)
	private String description;

	@OneToMany(mappedBy = "provider", fetch = FetchType.EAGER)
	private Set<ProductVariantEntity> providedProducts;

	@Column(name = "website")
	private String website;

	@Column(name = "image_url", nullable = false)
	private String imageUrl;

	@ManyToMany()
	@JoinTable(
		name = "product_base_provider_mapping",
		joinColumns = {@JoinColumn(name = "business_code")},
		inverseJoinColumns = {@JoinColumn(name = "product_base_id")}
	)
	private Set<ProductBaseEntity> productBases;
}
