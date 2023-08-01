package com.artiexh.model.rest.provider;

import com.artiexh.data.jpa.entity.ProviderEntity;
import jakarta.persistence.criteria.Predicate;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ProviderFilter {
	private String businessCode;

	private String businessName;

	public Specification<ProviderEntity> getSpecification() {
		return (root, cQuery, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (StringUtils.isNotBlank(businessCode)) {
				predicates.add(builder.like(root.get("businessCode"), "%" + businessCode.trim() + "%"));
			}
			if (StringUtils.isNotBlank(businessName)) {
				predicates.add(builder.like(root.get("id").get("businessName"), "%" + businessCode.trim() + "%"));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};

	}
}
