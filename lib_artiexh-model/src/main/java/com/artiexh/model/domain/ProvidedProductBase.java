package com.artiexh.model.domain;

import com.artiexh.data.jpa.entity.Color;
import com.artiexh.data.jpa.entity.ProvidedProductBaseId;
import com.artiexh.data.jpa.entity.Size;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import lombok.*;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProvidedProductBase {
	private ProvidedProductBaseId id;

	private BigDecimal priceAmount;

	private String priceUnit;

	private String description;

	private Color color;

	private List<Size> sizes;

	private Long maxLimit;

	private String[] allowConfig;

	private String providedProductFileUrl;

	private ProductBase productBase;
}
