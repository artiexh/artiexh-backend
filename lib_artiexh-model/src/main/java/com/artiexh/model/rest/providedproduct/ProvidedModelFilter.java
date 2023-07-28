package com.artiexh.model.rest.providedproduct;

import com.artiexh.data.jpa.entity.ProvidedModelEntity;
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
public class ProvidedModelFilter {
	private String businessCode;
	private long baseModelId;

	public Specification<ProvidedModelEntity> getSpecification() {
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
