package com.artiexh.model.rest.order.request;

import io.micrometer.common.util.StringUtils;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class PaymentQueryProperties {
	private String vnp_IpAddr;

	private String vnp_TransactionStatus;

	private String vnp_TransactionNo;

	private String vnp_ResponseCode;

	private String vnp_SecureHash;

	private String vnp_TxnRef;

	private String vnp_SecureHashType;

	private String vnp_OrderInfo;

	private String vnp_CardType;

	private String vnp_Amount;

	private String vnp_PayDate;

	private String vnp_BankCode;

	public Map<String, String> getMapProperties() {
		Map<String, String> properties = new HashMap<>();
		if (StringUtils.isNotBlank(vnp_TransactionStatus)) {
			properties.put("vnp_TransactionStatus", vnp_TransactionStatus);
		}
		return properties;
	}
}
