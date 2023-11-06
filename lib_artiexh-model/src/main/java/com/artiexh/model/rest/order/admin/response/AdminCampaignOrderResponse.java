package com.artiexh.model.rest.order.admin.response;

import com.artiexh.model.domain.OrderHistory;
import com.artiexh.model.rest.order.response.OrderDetailResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminCampaignOrderResponse extends AdminCampaignOrderResponsePage {
	private Set<OrderDetailResponse> orderDetails;
	private List<OrderHistory> orderHistories;
	private String shippingLabel;
	private String pickAddress;
	private String pickWard;
	private String pickDistrict;
	private String pickProvince;
	private String pickCountry;
	private String pickTel;
	private String pickName;
	private String pickEmail;
	private String returnAddress;
	private String returnWard;
	private String returnDistrict;
	private String returnProvince;
	private String returnCountry;
	private String returnTel;
	private String returnName;
	private String returnEmail;
}
