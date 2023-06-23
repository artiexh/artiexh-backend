package com.artiexh.model.rest.product.response;

import com.artiexh.model.domain.MerchStatus;
import com.artiexh.model.domain.MerchType;
import com.artiexh.model.domain.Province;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfoResponse {

	private Long id;
	private String thumbnailUrl;
	private MerchStatus status;
	private String currency;
	private String name;
	private float averageRate;
	private BigDecimal price;
	private MerchType type;
	private Long remainingQuantity;
	private ArtistInfo owner;
	private Instant publishDatetime;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ArtistInfo {
		private long id;
		private String username;
		private String displayName;
		private String avatarUrl;
		private Province province;
	}

}
