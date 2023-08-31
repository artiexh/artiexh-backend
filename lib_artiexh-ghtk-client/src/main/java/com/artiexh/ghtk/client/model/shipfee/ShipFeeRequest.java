package com.artiexh.ghtk.client.model.shipfee;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ShipFeeRequest {
	private String pickAddress;
	private String pickProvince;
	private String pickDistrict;
	private String pickWard;
	private String pickStreet;
	private String address;
	private String province;
	private String district;
	private String ward;
	private String street;
	private String weight;
	private String value;
	private String transport;
	private String deliverOption;
	private String tags;
}
