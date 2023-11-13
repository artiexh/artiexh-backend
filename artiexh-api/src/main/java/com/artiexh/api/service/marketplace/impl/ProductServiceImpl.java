package com.artiexh.api.service.marketplace.impl;

import com.artiexh.api.service.marketplace.JpaProductService;
import com.artiexh.api.service.marketplace.ProductService;
import com.artiexh.api.service.productinventory.ProductInventoryOpenSearchService;
import com.artiexh.data.jpa.entity.ProductEntity;
import com.artiexh.data.jpa.entity.ProductEntityId;
import com.artiexh.data.jpa.repository.ArtistRepository;
import com.artiexh.model.domain.Product;
import com.artiexh.model.domain.ProductSuggestion;
import com.artiexh.model.rest.marketplace.salecampaign.filter.ProductPageFilter;
import com.artiexh.model.rest.marketplace.salecampaign.response.ProductResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
	private final ArtistRepository artistRepository;
	private final JpaProductService jpaProductService;
	private final ProductInventoryOpenSearchService productInventoryOpenSearchService;

	@Override
	public Product create(ProductEntity entity) {
		Product result;
		try {
			result = jpaProductService.create(entity);
			productInventoryOpenSearchService.updateSaveCampaign(
				result.getProductInventory().getProductCode(),
				result.getPrice(),
				result.getCampaignSale()
			);
		} catch (Exception e) {
			log.warn("Insert product to db fail", e);
			throw e;
		}
		return result;
	}

	@Override
	public Page<ProductResponse> getAll(Pageable pageable, Query query) {
		Page<ProductEntityId> idPage = productInventoryOpenSearchService.getAll(pageable, query)
			.map(document -> new ProductEntityId(document.getProductCode(), document.getCampaign().getId()));
		return jpaProductService.getByProductInventoryId(idPage, pageable);
	}

	@Override
	public ProductResponse getByCampaignIdAndProductCode(long id, String productCode) {
		return jpaProductService.getById(new ProductEntityId(productCode, id));
	}

	@Override
	public Page<ProductResponse> getAllByArtist(String artistUsername, Pageable pageable, ProductPageFilter filter) {
		if (artistRepository.existsByUsername(artistUsername)) {
			filter.setArtistUsername(artistUsername);
			return getAll(pageable, filter.getQuery());
		} else {
			throw new EntityNotFoundException("Artist not found");
		}
	}

	@Override
	public Page<ProductSuggestion> getSuggestionInPage(Query query, Pageable pageable) {
		return productInventoryOpenSearchService.getSuggestionInPage(query, pageable);
	}

//	@Override
//	public Page<Product> getInPage(Query query, Pageable pageable) {
//		var productPage = openSearchProductService.getInPage(query, pageable);
//		return jpaProductService.fillProductPage(productPage);
//	}
//
//	@Override
//	public Product getDetail(long id) {
//		return jpaProductService.getDetail(id);
//	}
//
//	@Override
//	public Product create(long artistId, Product product, ProductInCampaignEntity productInCampaign) {
//		Product result;
//		try {
//			result = jpaProductService.create(artistId, product, productInCampaign);
//			openSearchProductService.save(result);
//		} catch (Exception e) {
//			log.warn("Insert product to db fail", e);
//			throw e;
//		}
//		return result;
//	}
//
//	@Override
//	public Product update(long artistId, Product product) {
//		Product result;
//		try {
//			result = jpaProductService.update(artistId, product);
//			openSearchProductService.update(result);
//		} catch (Exception e) {
//			log.warn("Update product to db fail", e);
//			throw e;
//		}
//		return result;
//	}
//
//	@Override
//	public void delete(long userId, long productId) {
//		try {
//			jpaProductService.delete(userId, productId);
//			openSearchProductService.delete(productId);
//		} catch (Exception e) {
//			log.warn("Delete product from db fail", e);
//			throw e;
//		}
//
//	}
}
