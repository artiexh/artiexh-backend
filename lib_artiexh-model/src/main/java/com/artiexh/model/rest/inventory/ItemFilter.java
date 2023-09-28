package com.artiexh.model.rest.inventory;

import com.artiexh.data.jpa.entity.InventoryItemEntity;
import com.artiexh.data.jpa.entity.ProductBaseEntity;
import com.artiexh.data.jpa.entity.ProductVariantEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemFilter {
	@JsonSerialize(using = ToStringSerializer.class)
	@JsonIgnore
	private Long artistId;
	private String name;
	private Set<Long> categoryIds;
	private Set<String> providerIds;

	public Specification<InventoryItemEntity> getSpecification() {
		return (root, cQuery, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(builder.equal(root.get("artist").get("id"), artistId));

			if (name != null) {
				predicates.add(builder.like(root.get("name"), "%" + name + "%"));
			}

			if (categoryIds != null && !categoryIds.isEmpty()) {
				Join<InventoryItemEntity, ProductBaseEntity> productBaseJoin = root.join("variant").join("productBase");
				predicates.add(productBaseJoin.get("category").get("id").in(categoryIds));
			}

			if (providerIds != null && !providerIds.isEmpty()) {
				Join<InventoryItemEntity, ProductVariantEntity> productVariantJoin = root.join("variant");
				predicates.add(productVariantJoin.get("providerConfigs").get("provider").get("id").in(providerIds));
			}

			return builder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
