package com.artiexh.model.rest.basemodel;

import com.artiexh.data.jpa.entity.BaseModelEntity;
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
public class BaseModelFilter {
	private String name;

	public Specification<BaseModelEntity> getSpecification() {
		return (root, cQuery, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (StringUtils.isNotBlank(name)) {
				predicates.add(builder.like(root.get("name"), "%" + name.trim() + "%"));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};

	}

}
