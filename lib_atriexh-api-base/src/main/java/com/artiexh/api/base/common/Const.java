package com.artiexh.api.base.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Const {

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static final class SystemConfigKey {
		public static final String ARTIEXH_ADDRESS = "artiexh.address";
		public static final String ARTIEXH_WARD_ID = "artiexh.ward.id";
		public static final String ARTIEXH_PHONE = "artiexh.phone";
	}

}
