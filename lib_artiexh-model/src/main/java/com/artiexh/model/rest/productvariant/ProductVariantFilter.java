package com.artiexh.model.rest.productvariant;

import com.artiexh.data.jpa.entity.ProductVariantCombinationEntity;
import com.artiexh.data.jpa.entity.ProductVariantEntity;
import com.artiexh.model.domain.Model3DCode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
import com.fasterxml.jackson.databind.ser.impl.StringCollectionSerializer;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantFilter {
	@JsonSerialize(using = ToStringSerializer.class)
	@NotNull
	private Long productBaseId;

	@JsonSerialize(using = StringCollectionSerializer.class)
	private Set<Long> optionValueIds;

	public Specification<ProductVariantEntity> getSpecification() {
		return (root, cQuery, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			Join<ProductVariantCombinationEntity, ProductVariantEntity> joinVariantCombination= root.join("variantCombinations");

			if (productBaseId != null) {
				predicates.add(builder.equal(root.get("productBaseId"), productBaseId));
			}
			if (optionValueIds != null) {
				predicates.add(joinVariantCombination.get("id").get("optionValueId").in(Arrays.asList(optionValueIds)));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
