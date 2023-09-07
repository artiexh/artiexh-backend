package com.artiexh.model.rest.provider;

import com.artiexh.data.jpa.entity.CollectionEntity;
import com.artiexh.data.jpa.entity.ProvidedProductBaseEntity;
import com.artiexh.data.jpa.entity.ProviderEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CollectionFilter {
	@JsonSerialize(using = StringArraySerializer.class)
	private Long[] productBaseIds;
	@JsonIgnore
	private String businessCode;

	public Specification<CollectionEntity> getSpecification() {
		return (root, cQuery, builder) -> {
			List<Predicate> predicates = new ArrayList<>();

			Join<CollectionEntity, ProvidedProductBaseEntity> joinProduct = root.join("providedProducts");
			predicates.add(builder.equal(joinProduct.get("providedProductBaseId").get("businessCode"), businessCode));
			if (productBaseIds != null && productBaseIds.length > 0) {
				predicates.add(joinProduct.get("providedProductBaseId").get("productBaseId").in(Arrays.asList(productBaseIds)));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
