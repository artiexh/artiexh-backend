package com.artiexh.model.rest.provider;

import com.artiexh.data.jpa.entity.ProvidedProductBaseEntity;
import com.artiexh.data.jpa.entity.ProviderEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.impl.StringArraySerializer;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProviderFilter {
	@JsonSerialize(using = StringArraySerializer.class)
	private Long[] productBaseIds;
	private String businessName;

	public Specification<ProviderEntity> getSpecification() {
		return (root, cQuery, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (StringUtils.isNotEmpty(businessName)) {
				predicates.add(builder.like(root.get("businessName"), "%" + businessName + "%"));
			}
			if (productBaseIds != null && productBaseIds.length > 0) {
				Join<ProviderEntity, ProvidedProductBaseEntity> joinProviderProvidedProduct = root.join("providedProducts");
				predicates.add(joinProviderProvidedProduct.get("id").get("productBaseId").in(Arrays.asList(productBaseIds)));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};
	}
}