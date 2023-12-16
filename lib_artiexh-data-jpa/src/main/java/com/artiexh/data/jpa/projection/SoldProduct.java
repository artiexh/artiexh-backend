package com.artiexh.data.jpa.projection;

import java.math.BigDecimal;

public interface SoldProduct {
	String getProductCode();
	Long getSoldQuantity();
	Long getQuantity();
	BigDecimal getPriceAmount();

}
