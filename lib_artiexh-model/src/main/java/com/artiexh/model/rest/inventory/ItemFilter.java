package com.artiexh.model.rest.inventory;

import com.artiexh.data.jpa.entity.InventoryItemEntity;
import com.artiexh.data.jpa.entity.ProductOptionEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.criteria.Predicate;
import lombok.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemFilter {
	@JsonSerialize(using = ToStringSerializer.class)
	@JsonIgnore
	private Long artistId;

	public Specification<InventoryItemEntity> getSpecification() {
		return (root, cQuery, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(builder.equal(root.get("artist").get("id"), artistId));
			return builder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
