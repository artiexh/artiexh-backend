package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.CartEntity;
import com.artiexh.data.jpa.entity.CartItemEntity;
import com.artiexh.data.jpa.entity.ProductEntity;
import com.artiexh.model.domain.Cart;
import com.artiexh.model.domain.CartItem;
import com.artiexh.model.domain.Product;
import com.artiexh.model.domain.Shop;
import com.artiexh.model.rest.cart.response.CartItemResponse;
import com.artiexh.model.rest.cart.response.CartResponse;
import com.artiexh.model.rest.cart.response.ShopInCartResponse;
import com.artiexh.model.rest.cart.response.ShopItemsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {ProductMapper.class, AccountMapper.class, ProductAttachMapper.class, ShopMapper.class, ArtistMapper.class}
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
	Product productEntityToCartItemProduct(ProductEntity productEntity);

	CartEntity domainToEntity(Cart cart);

	@Mapping(target = "shopItems", source = "cartItems", qualifiedByName = "artistItemsResponseMapping")
	CartResponse domainToCartResponse(Cart cart);

	@Named("artistItemsResponseMapping")
	default Set<ShopItemsResponse> artistItemsResponseMapping(Set<CartItem> cartItems) {
		if (cartItems == null) {
			return Collections.emptySet();
		}
		return cartItems.stream()
			.collect(Collectors.groupingBy(
				cartItem -> cartItem.getProduct().getShop(),
				Collectors.mapping(this::domainToCartItemResponse, Collectors.toSet())
			))
			.entrySet().stream()
			.map(entry -> new ShopItemsResponse(
				artistToShopInfoResponse(entry.getKey()),
				entry.getValue()
			))
			.collect(Collectors.toSet());
	}

	@Mapping(target = "imageUrl", source = "shopImageUrl")
	ShopInCartResponse artistToShopInfoResponse(Shop shop);

	@Mapping(target = "id", source = "product.id")
	@Mapping(target = "status", source = "product.status")
	//@Mapping(target = "price.unit", source = "product.price.unit")
	@Mapping(target = "name", source = "product.name")
	@Mapping(target = "thumbnailUrl", source = "product.thumbnailUrl")
	//@Mapping(target = "price.amount", source = "product.price.amount")
	@Mapping(target = "price", source = "product.price")
	@Mapping(target = "description", source = "product.description")
	@Mapping(target = "type", source = "product.type")
	@Mapping(target = "remainingQuantity", source = "product.remainingQuantity")
	@Mapping(target = "publishDatetime", source = "product.publishDatetime")
	@Mapping(target = "maxItemsPerOrder", source = "product.maxItemsPerOrder")
	@Mapping(target = "deliveryType", source = "product.deliveryType")
	CartItemResponse domainToCartItemResponse(CartItem cartItem);

	CartItemEntity domainToEntity(CartItem cartItem);

}
