package com.artiexh.model.product.request;

import com.artiexh.model.domain.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
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
	private LocalDateTime publishDatetime;
	private Long maxItemsPerOrder;
	private DeliveryType deliveryType;
	private Set<String> categories;
	private Set<String> tags;
	private Set<MerchAttach> attaches;
}
