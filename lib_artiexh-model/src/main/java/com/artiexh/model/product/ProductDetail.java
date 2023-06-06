package com.artiexh.model.product;

import com.artiexh.model.domain.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ProductDetail extends ProductInfo {
	@JsonIgnore
	private long ownerId;
	private String description;
	private Instant publishDatetime;
	private Long maxItemsPerOrder;
	private DeliveryType deliveryType;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Set<String> categories;
	private Set<String> tags;
	private Set<MerchAttach> attaches;
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(readOnly = true)
	private Map<Long, String> categoryInfo;
}
