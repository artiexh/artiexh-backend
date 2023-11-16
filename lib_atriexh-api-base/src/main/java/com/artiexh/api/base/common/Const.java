package com.artiexh.api.base.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Const {

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class SystemConfigKey {
		public static final String ARTIEXH_PICK_ADDRESS = "artiexh.pick.address";
		public static final String ARTIEXH_PICK_WARD_ID = "artiexh.pick.ward.id";
		public static final String ARTIEXH_PICK_PHONE = "artiexh.pick.phone";
		public static final String ARTIEXH_PICK_EMAIL = "artiexh.pick.email";
		public static final String ARTIEXH_PICK_NAME = "artiexh.pick.name";
		public static final String DEFAULT_PROFIT_PERCENTAGE = "artiexh.product.profit.percentage";
	}

}
