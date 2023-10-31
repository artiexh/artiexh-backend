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
					CampaignType.MARKETPLACE_VIEW_TYPE.stream()
						.map(CampaignType::getByteValue)
						.collect(Collectors.toSet())
				));
			}
			return builder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
