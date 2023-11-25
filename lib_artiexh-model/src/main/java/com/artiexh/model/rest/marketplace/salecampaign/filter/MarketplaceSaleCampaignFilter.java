package com.artiexh.model.rest.marketplace.salecampaign.filter;

import com.artiexh.data.jpa.entity.CampaignSaleEntity;
import com.artiexh.model.domain.CampaignSaleStatus;
import com.artiexh.model.domain.CampaignType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarketplaceSaleCampaignFilter {
	private Long ownerId;
	private String username;
	private CampaignType campaignType;
	private Instant from = Instant.now();
	private Instant to;
	@JsonIgnore
	private Instant publicDate = Instant.now();
	private CampaignSaleStatus status;

	public Specification<CampaignSaleEntity> getSpecification() {
		return (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (status != null) {
				predicates.add(builder.equal(root.get("status"), status.getByteValue()));
			}

			if (ownerId != null) {
				predicates.add(builder.equal(root.get("owner").get("id"), ownerId));
			}

			if (username != null) {
				predicates.add(builder.equal(root.get("owner").get("username"), username));
			}

			if (to != null) {
				List<Predicate> toPredicate = new ArrayList<>();
				toPredicate.add(builder.lessThanOrEqualTo(root.get("from"), to));
				toPredicate.add(builder.lessThanOrEqualTo(root.get("to"), to));
				predicates.add(builder.or(toPredicate.toArray(new Predicate[0])));
			}

			if (from != null) {
				List<Predicate> fromPredicate = new ArrayList<>();
				fromPredicate.add(builder.greaterThanOrEqualTo(root.get("from"), from));
				fromPredicate.add(builder.greaterThanOrEqualTo(root.get("to"), from));
				predicates.add(builder.or(fromPredicate.toArray(new Predicate[0])));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};
	}

	public Specification<CampaignSaleEntity> getMarketplaceSpecification() {
		Specification<CampaignSaleEntity> marketplaceCampaignSpec = (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();

			predicates.add(builder.lessThanOrEqualTo(root.get("publicDate"), publicDate));

			predicates.add(builder.equal(root.get("status"), CampaignSaleStatus.ACTIVE.getByteValue()));

			if (campaignType != null && CampaignType.MARKETPLACE_VIEW_TYPE.contains(campaignType)) {
				predicates.add(builder.equal(root.get("type"), campaignType.getByteValue()));
			} else {
				predicates.add(root.get("type").in(
					CampaignType.MARKETPLACE_VIEW_TYPE.stream()
						.map(CampaignType::getByteValue)
						.collect(Collectors.toSet())
				));
			}

			return builder.and(predicates.toArray(new Predicate[0]));
		};
		return getSpecification().and(marketplaceCampaignSpec);
	}
}
