package com.artiexh.model.rest.product;

import com.artiexh.model.domain.DeliveryType;
import com.artiexh.model.domain.MerchAttach;
import com.artiexh.model.domain.MerchCategory;
import com.artiexh.model.domain.PaymentMethod;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class ProductDetail extends ProductInfo {
	private String description;

	private Long maxItemsPerOrder;

	@NotNull()
	private DeliveryType deliveryType;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotNull
	private Long categoryId;

	@NotNull
	private Set<String> tags;

	@NotEmpty
	private Set<MerchAttach> attaches;

	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(accessMode = Schema.AccessMode.READ_ONLY)
	private MerchCategory categoryInfo;

	@Future()
	private Instant startDatetime;

	@Future()
	private Instant endDateTime;

	@NotEmpty
	private PaymentMethod[] paymentMethod;
}
