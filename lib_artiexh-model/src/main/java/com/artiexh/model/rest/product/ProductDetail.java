package com.artiexh.model.rest.product;

import com.artiexh.model.domain.DeliveryType;
import com.artiexh.model.domain.MerchAttach;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ProductDetail extends ProductInfo {
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private long ownerId;
	private String description;
	private Instant publishDatetime;
	private Long maxItemsPerOrder;
	private DeliveryType deliveryType;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Set<String> categories;
	private Set<String> tags = Set.of();
	private Set<MerchAttach> attaches = Set.of();
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	private Map<Long, String> categoryInfo;
}
