package com.artiexh.model.rest.product.response;

import com.artiexh.model.domain.DeliveryType;
import com.artiexh.model.domain.Money;
import com.artiexh.model.domain.PaymentMethod;
import com.artiexh.model.domain.ProductAttach;
import com.artiexh.model.domain.ProductStatus;
import com.artiexh.model.domain.ProductType;
import com.artiexh.model.domain.Shop;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import java.time.Instant;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

	@JsonSerialize(using = ToStringSerializer.class)
	private String id;
	private String name;
	private String thumbnailUrl;
	private ProductStatus status;
	private Money price;
	private float averageRate;
	private ProductType type;
	private long remainingQuantity;
	private Owner owner;
	private Instant publishDatetime;
	private String description;
	private Integer maxItemsPerOrder;
	private DeliveryType deliveryType;
	private Set<String> tags;
	private Set<ProductAttach> attaches;
	private Category category;
	private Set<PaymentMethod> paymentMethods;
	private Shop shop;
	private Float weight;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Owner {

		@JsonSerialize(using = ToStringSerializer.class)
		private String id;
		private String username;
		private String displayName;
		private String avatarUrl;
		private Province province;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Province {

		@JsonSerialize(using = ToStringSerializer.class)
		private Short id;
		private String name;
		private Country country;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Country {

		@JsonSerialize(using = ToStringSerializer.class)
		private Short id;
		private String name;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Category {

		@JsonSerialize(using = ToStringSerializer.class)
		private String id;
		private String name;
	}
}
