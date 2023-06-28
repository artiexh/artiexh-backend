package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.CartEntity;
import com.artiexh.data.jpa.entity.CartItemEntity;
import com.artiexh.model.domain.Artist;
import com.artiexh.model.domain.Cart;
import com.artiexh.model.domain.CartItem;
import com.artiexh.model.rest.cart.response.ArtistCartInfoResponse;
import com.artiexh.model.rest.cart.response.ArtistItemsResponse;
import com.artiexh.model.rest.cart.response.CartItemResponse;
import com.artiexh.model.rest.cart.response.CartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {ProductMapper.class, AccountMapper.class, ProductAttachMapper.class}
)
public interface CartMapper {

	@Mapping(target = "cartItems", source = "cartItems")
	Cart entityToDomain(CartEntity cartEntity);

	CartEntity domainToEntity(Cart cart);

	@Mapping(target = "artistItems", source = "cartItems", qualifiedByName = "artistItemsResponseMapping")
	CartResponse domainToCartResponse(Cart cart);

	@Named("artistItemsResponseMapping")
	default Set<ArtistItemsResponse> artistItemsResponseMapping(Set<CartItem> cartItems) {
		if (cartItems == null) {
			return Collections.emptySet();
		}
		return cartItems.stream()
			.collect(Collectors.groupingBy(
				cartItem -> cartItem.getProduct().getOwner(),
				Collectors.mapping(this::domainToCartItemResponse, Collectors.toSet())
			))
			.entrySet().stream()
			.map(entry -> new ArtistItemsResponse(
				artistToArtistCartInfo(entry.getKey()),
				entry.getValue()
			))
			.collect(Collectors.toSet());
	}

	ArtistCartInfoResponse artistToArtistCartInfo(Artist artist);

	@Mapping(target = "id", source = "product.id")
	@Mapping(target = "status", source = "product.status")
	@Mapping(target = "currency", source = "product.price.unit")
	@Mapping(target = "name", source = "product.name")
	@Mapping(target = "price", source = "product.price.amount")
	@Mapping(target = "description", source = "product.description")
	@Mapping(target = "type", source = "product.type")
	@Mapping(target = "remainingQuantity", source = "product.remainingQuantity")
	@Mapping(target = "publishDatetime", source = "product.publishDatetime")
	@Mapping(target = "maxItemsPerOrder", source = "product.maxItemsPerOrder")
	@Mapping(target = "deliveryType", source = "product.deliveryType")
	@Mapping(target = "attaches", source = "product.attaches")
	CartItemResponse domainToCartItemResponse(CartItem cartItem);

	CartItem entityToDomain(CartItemEntity cartItemEntity);

	CartItemEntity domainToEntity(CartItem cartItem);

}
