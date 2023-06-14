package com.artiexh.model.mapper;

import com.artiexh.model.domain.MerchStatus;
import com.artiexh.model.domain.PaymentMethod;
import org.mapstruct.Mapper;

@Mapper
public interface PaymentMethodMapper {
	default Integer toValue(PaymentMethod paymentMethod) {
		return paymentMethod.getValue();
	}

	default PaymentMethod toPaymentMethod(Integer value) {
		return PaymentMethod.fromValue(value);
	}

}
