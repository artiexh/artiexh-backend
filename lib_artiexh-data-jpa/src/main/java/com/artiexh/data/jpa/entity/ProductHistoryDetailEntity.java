package com.artiexh.data.jpa.entity;

import com.artiexh.data.jpa.entity.embededmodel.ProductHistoryEntityDetailId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "product_history_detail")
public class ProductHistoryDetailEntity {
	@EmbeddedId
	private ProductHistoryEntityDetailId id;

	private Long quantity;
}
