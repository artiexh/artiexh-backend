package com.artiexh.model.domain;

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
	private Set<ProductInventory> products;
	private Set<Subscription> subscriptionsFrom;
	private String bankAccount;
	private String bankName;
	private String phone;
	private String address;
	private Ward ward;
}
