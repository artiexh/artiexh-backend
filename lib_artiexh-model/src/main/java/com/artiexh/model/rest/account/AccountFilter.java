package com.artiexh.model.rest.account;

import com.artiexh.data.jpa.entity.AccountEntity;
import com.artiexh.model.domain.CampaignType;
import com.artiexh.model.domain.Role;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccountFilter {
	private Role role;
	private String username;

	public Specification<AccountEntity> getSpecification() {
		return (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (role != null) {
				predicates.add(builder.equal(root.get("role"), role.getByteValue()));
			}

			if (StringUtils.isNotBlank(username)) {
				predicates.add(builder.like(root.get("username"), "%" + username + "%"));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
