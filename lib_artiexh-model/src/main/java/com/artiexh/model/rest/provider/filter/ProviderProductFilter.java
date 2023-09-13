package com.artiexh.model.rest.provider.filter;

import com.artiexh.data.jpa.entity.*;
import com.artiexh.model.domain.Model3DCode;
import com.artiexh.model.domain.ProvidedProductType;
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
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProviderProductFilter {
	@JsonSerialize(using = StringArraySerializer.class)
	private Long[] productBaseIds;
//	@JsonIgnore
	private String businessCode;

	private String[] optionIds;

	private String[] optionValues;

	private Model3DCode model3DCode;

	public Specification<ProvidedProductBaseEntity> getSpecification() {
		return (root, cQuery, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(builder.equal(root.get("providedProductBaseId").get("businessCode"), businessCode));
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
