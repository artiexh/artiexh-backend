package com.artiexh.model.rest.transaction;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderTransactionResponse {
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	private String transactionNo;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long orderId;

	private BigDecimal priceAmount;

	private String bankCode;

	private String cardType;

	private String orderInfo;

	private LocalDateTime payDate;

	private String responseCode;

	private String message;

	private String transactionStatus;
}