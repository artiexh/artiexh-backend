package com.artiexh.model.rest.shop;

import com.artiexh.data.jpa.entity.ArtistEntity;
import com.artiexh.data.jpa.entity.CampaignEntity;
import com.artiexh.model.domain.CampaignStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShopCampaignFilter {
	@JsonIgnore
	private String username;
	@JsonIgnore
	private CampaignStatus campaignStatus;
	private Boolean isPrivate;

	public Specification<CampaignEntity> getSpecification() {
		return (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			if (username != null) {
				predicates.add(builder.like(root.join("owner").get("username"), username));
			}
			if (campaignStatus != null) {
				predicates.add(builder.equal(root.get("status"), campaignStatus.getByteValue()));
			}
			if (isPrivate != null) {
				predicates.add(builder.equal(root.get("isPrivate"), isPrivate));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
