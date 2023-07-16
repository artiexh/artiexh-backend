package com.artiexh.model.rest.providedproduct;

import com.artiexh.data.jpa.entity.BaseModelEntity;
import com.artiexh.data.jpa.entity.ProvidedProductEntity;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProvidedProductFilter {
	private String businessCode;
	private long baseModelId;
}
