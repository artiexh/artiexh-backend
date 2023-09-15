package com.artiexh.model.rest.provider.filter;

import com.artiexh.data.jpa.entity.*;
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
public class ProviderProductFilter {
	@JsonSerialize(using = StringArraySerializer.class)
	private Long[] productBaseIds;

	private String businessCode;

	private String[] optionIds;

	private String[] optionValues;

	private Model3DCode model3DCode;

	public Specification<ProductVariantEntity> getSpecification() {
		return (root, cQuery, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (businessCode != null) {
				predicates.add(builder.equal(root.get("providedProductBaseId").get("businessCode"), businessCode));
			}
			if (productBaseIds != null && productBaseIds.length > 0) {
				predicates.add(root.get("id").get("productBaseId").in(Arrays.asList(productBaseIds)));
			}
			if (model3DCode != null) {
				predicates.add(builder.equal(root.join("productBase").get("model3DCode"), model3DCode.getByteValue()));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
