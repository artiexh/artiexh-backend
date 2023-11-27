package com.artiexh.model.rest.artist.request;

import com.artiexh.data.jpa.entity.ArtistEntity;
import com.artiexh.data.jpa.entity.CampaignSaleEntity;
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
public class ArtistFilter {
	private String name;
	public Specification<ArtistEntity> getSpecification(Long rootAdminId) {
		return (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(builder.notEqual(root.get("id"),rootAdminId));
			if (StringUtils.isNotBlank(name)) {
				predicates.add(builder.like(root.get("displayName"), "%" + name + "%"));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
