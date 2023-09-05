package com.artiexh.data.jpa.projection;

import java.math.BigDecimal;

public interface Bill {
	Long getOrderId();

	BigDecimal getOrderAmount();

	String getPriceUnit();

	Long getOwnerId();

	Byte getStatus();
}
