package com.artiexh.model.rest.providedproduct;

import com.artiexh.data.jpa.entity.BaseModelEntity;
import com.artiexh.data.jpa.entity.ProvidedProductEntity;
import com.artiexh.data.jpa.entity.ProviderEntity;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProvidedProductFilter {
	private String businessCode;
	private long baseModelId;

	public Specification<ProvidedProductEntity> getSpecification() {
		return (root, cQuery, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (StringUtils.isNotBlank(businessCode)) {
				predicates.add(builder.like(root.get("id").get("businessCode"), "%" + businessCode.trim() + "%"));
			}
			if (baseModelId != 0) {
				predicates.add(builder.equal(root.get("baseModelId"), baseModelId));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};

	}
}
