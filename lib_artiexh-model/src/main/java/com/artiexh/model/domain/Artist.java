package com.artiexh.model.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Artist extends User {
	private Set<Merch> merch;
	private Set<Subscription> subscriptionsFrom;
}
