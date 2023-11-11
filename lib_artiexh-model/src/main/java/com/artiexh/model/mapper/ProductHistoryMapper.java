package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.ProductHistoryEntity;
import com.artiexh.model.domain.*;
import com.artiexh.model.rest.producthistory.ProductHistoryPageResponse;
import com.artiexh.model.rest.producthistory.ProductHistoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {ProductInventoryMapper.class, CampaignMapper.class}
)
public interface ProductHistoryMapper {
	@Mapping(target = "productHistoryDetails", ignore = true)
	ProductHistory entityToDomainWithoutProductHistoryDetail(ProductHistoryEntity entity);
	ProductHistory entityToDomain(ProductHistoryEntity entity);
	ProductHistoryPageResponse domainToPageResponse(ProductHistory productHistory);
	ProductHistoryResponse domainToResponse(ProductHistory productHistory);

	default Byte toValue(SourceCategory sourceCategory) {
		return sourceCategory == null ? null : sourceCategory.getByteValue();
	}

	default SourceCategory getSourceCategory(Byte value) {
		return SourceCategory.fromValue(value);
	}

	default Byte toValue(ProductHistoryAction action) {
		return action == null ? null : action.getByteValue();
	}

	default ProductHistoryAction getAction(Byte value) {
		return ProductHistoryAction.fromValue(value);
	}

}
