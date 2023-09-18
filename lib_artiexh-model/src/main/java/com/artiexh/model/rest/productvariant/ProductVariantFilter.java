package com.artiexh.model.rest.productvariant;

import com.artiexh.data.jpa.entity.ProductVariantEntity;
import com.artiexh.model.domain.Model3DCode;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
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
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantFilter {
	@JsonSerialize(using = StringArraySerializer.class)
	private Long[] productBaseIds;

	private String businessCode;

	private Long[] optionIds;

	private Long[] optionValueIds;

	private Model3DCode model3DCode;

	public Specification<ProductVariantEntity> getSpecification() {
		return (root, cQuery, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (businessCode != null) {
				predicates.add(builder.equal(root.get("providedProductBaseId").get("businessCode"), businessCode));
			}
			if (productBaseIds != null && productBaseIds.length > 0) {
				predicates.add(root.get("productBaseId").in(Arrays.asList(productBaseIds)));
			}
			if (model3DCode != null) {
				predicates.add(builder.equal(root.join("productBase").get("model3DCode"), model3DCode.getByteValue()));
			}
			if (optionValueIds != null && optionValueIds.length > 0) {
				predicates.add(root.join("variantCombinations").get("id").get("optionValueId").in(Arrays.asList(optionValueIds)));
			}
			if (optionIds != null && optionIds.length > 0) {
				predicates.add(root.join("variantCombinations").get("optionId").in(Arrays.asList(optionIds)));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
