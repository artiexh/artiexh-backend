package com.artiexh.model.rest.artist.filter;

import com.artiexh.data.jpa.entity.CampaignEntity;
import com.artiexh.model.domain.CampaignType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArtistCampaignFilter {
	@JsonIgnore
	private long id;
	private CampaignType campaignType;
	private Instant from = Instant.now();
	private Instant to;

	public Specification<CampaignEntity> getSpecification() {
		return (root, query, builder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(builder.equal(root.get("isPublished"), true));
			predicates.add(builder.equal(root.get("owner").get("id"), id));
			if (campaignType != null) {
				predicates.add(builder.equal(root.get("type"), campaignType.getByteValue()));
			}

			if(campaignType != null && CampaignType.ARTIST_VIEW_TYPE.contains(campaignType)) {
				predicates.add(builder.equal(root.get("type"), campaignType.getByteValue()));
			} else {
				predicates.add(root.get("type").in(
					CampaignType.ARTIST_VIEW_TYPE.stream()
						.map(CampaignType::getByteValue)
						.collect(Collectors.toSet())
				));
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
}
