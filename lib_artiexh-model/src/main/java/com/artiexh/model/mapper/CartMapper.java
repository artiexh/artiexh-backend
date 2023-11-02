package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.CartEntity;
import com.artiexh.data.jpa.entity.CartItemEntity;
import com.artiexh.data.jpa.entity.ProductEntity;
import com.artiexh.model.domain.Campaign;
import com.artiexh.model.domain.Cart;
import com.artiexh.model.domain.CartItem;
import com.artiexh.model.domain.Product;
import com.artiexh.model.rest.cart.response.CartCampaignResponse;
import com.artiexh.model.rest.cart.response.CartItemResponse;
import com.artiexh.model.rest.cart.response.CartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {ProductMapper.class, AccountMapper.class, ProductAttachMapper.class, ShopMapper.class, ArtistMapper.class, CampaignMapper.class}
)
public interface CartMapper {

	@Mapping(target = "cartItems", source = "cartItems")
	Cart entityToDomain(CartEntity cartEntity);

	@Mapping(target = "product", qualifiedByName = "productEntityToCartItemProduct")
	CartItem entityToDomain(CartItemEntity cartItemEntity);

	@Named("productEntityToCartItemProduct")
	@Mapping(target = "tags", ignore = true)
	@Mapping(target = "attaches", ignore = true)
	@Mapping(target = "owner", qualifiedByName = "basicArtistInfo")
	@Mapping(target = "shop", qualifiedByName = "basicShopInfo")
	@Mapping(target = "price.amount", source = "priceAmount")
	@Mapping(target = "price.unit", source = "priceUnit")
	@Mapping(target = "thumbnailUrl", source = "attaches", qualifiedByName = "getProductThumbnailUrl")
	@Mapping(target = "productInCampaign", ignore = true)
	Product productEntityToCartItemProduct(ProductEntity productEntity);

	@Mapping(target = "id", ignore = true)
	CartEntity domainToEntity(Cart cart);

	@Mapping(target = "campaigns", source = "cartItems", qualifiedByName = "campaignsResponseMapping")
	CartResponse domainToCartResponse(Cart cart);

	@Named("campaignsResponseMapping")
	default Set<CartCampaignResponse> campaignsResponseMapping(Set<CartItem> cartItems) {
		if (cartItems == null) {
			return Collections.emptySet();
		}
		return cartItems.stream()
			.collect(Collectors.groupingBy(
				cartItem -> cartItem.getProduct().getCampaign(),
				Collectors.mapping(this::domainToCartItemResponse, Collectors.toSet())
			))
			.entrySet().stream()
			.map(this::entryProductByCampaignToCartCampaignResponse)
			.collect(Collectors.toSet());
	}

	@Mapping(target = "campaign", source = "entry.key")
	@Mapping(target = "items", source = "entry.value")
	CartCampaignResponse entryProductByCampaignToCartCampaignResponse(Map.Entry<Campaign, Set<CartItemResponse>> entry);

	@Mapping(target = "id", source = "product.id")
	@Mapping(target = "status", source = "product.status")
	@Mapping(target = "name", source = "product.name")
	@Mapping(target = "thumbnailUrl", source = "product.thumbnailUrl")
	@Mapping(target = "price", source = "product.price")
	@Mapping(target = "description", source = "product.description")
	@Mapping(target = "type", source = "product.type")
	@Mapping(target = "remainingQuantity", source = "product.remainingQuantity")
	@Mapping(target = "maxItemsPerOrder", source = "product.maxItemsPerOrder")
	@Mapping(target = "deliveryType", source = "product.deliveryType")
	CartItemResponse domainToCartItemResponse(CartItem cartItem);

	CartItemEntity domainToEntity(CartItem cartItem);

}
