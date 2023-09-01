package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_transaction")
@Builder(toBuilder = true)
@EntityListeners(AuditingEntityListener.class)
public class OrderTransactionEntity {
	@Id
	@Tsid
	@Column(name = "id", nullable = false)
	private Long id;
	private String transactionNo;

	@Column(name = "order_id", nullable = false)
	private Long orderId;

	private BigDecimal priceAmount;

	private String bankCode;

	private String cardType;

	private String orderInfo;

	private LocalDateTime payDate;

	private String responseCode;

	private String transactionStatus;

}
