package com.artiexh.model.domain;

import com.artiexh.data.jpa.entity.ShopEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Artist extends User {
	private Set<Product> products;
	private Set<Subscription> subscriptionsFrom;
	private Province province;
	private Set<Shop> shop;
}
