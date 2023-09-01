package com.artiexh.model.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class OrderTransaction {
	private Long id;

	private String transactionNo;

	private Long orderId;

	private BigDecimal priceAmount;

	private String bankCode;

	private String cardType;

	private String orderInfo;

	private LocalDateTime payDate;

	private String responseCode;

	private String transactionStatus;
}
