package com.artiexh.model.rest.campaign.request;

import com.artiexh.data.jpa.entity.CampaignEntity;
import com.artiexh.model.domain.CampaignStatus;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignRequestFilter {
	private CampaignStatus status;
	private String ownerId;
	private String providerId;

	public Specification<CampaignEntity> getSpecification() {
		return ((root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (status != null) {
				predicates.add(builder.equal(root.get("status"), status));
			}

			if (ownerId != null) {
				predicates.add(builder.equal(root.get("owner").get("id"), ownerId));
			}

			if (providerId != null) {
				predicates.add(builder.equal(root.get("providerId"), providerId));
			}

			return builder.and(predicates.toArray(new Predicate[0]));
		});
	}

}
