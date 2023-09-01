package com.artiexh.model.domain;

import com.artiexh.data.jpa.entity.ProvidedProductBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.SuperBuilder;

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

	private Set<ProvidedProductBase> providedProducts;
}
