package com.artiexh.model.domain;

import lombok.*;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Provider {
	private String businessCode;

	private String businessName;

	private String address;

	private String contactName;

	private String email;

	private String phone;

	private String description;

	private Set<ProductVariant> productVariants;

	private Set<ProductBase> productBases;

	private String website;

	private String imageUrl;
}
