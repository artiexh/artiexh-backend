package com.artiexh.model.rest.product.request;

import com.artiexh.model.domain.DeliveryType;
import com.artiexh.model.domain.MerchAttach;
import com.artiexh.model.domain.MerchStatus;
import com.artiexh.model.domain.MerchType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UpdateProductRequest {
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	private Long id;
	private MerchStatus status;
	private String name;
	private Double price;
	private String description;
	private MerchType type;
	private Long remainingQuantity;
	private Instant publishDatetime;
	private Long maxItemsPerOrder;
	private DeliveryType deliveryType;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Set<String> categories;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	private Map<Long, String> categoryInfo;
	private Set<String> tags;
	private Set<MerchAttach> attaches;
}
