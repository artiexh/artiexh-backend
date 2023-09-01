package com.artiexh.data.jpa.projection;

import java.math.BigDecimal;

public interface Bill {
	BigDecimal getPriceAmount();

	String getPriceUnit();
}
