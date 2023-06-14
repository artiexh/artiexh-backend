package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.CartEntity;
import com.artiexh.model.domain.Artist;
import com.artiexh.model.domain.Cart;
import com.artiexh.model.domain.CartItem;
import com.artiexh.model.rest.cart.ArtistCartInfoResponse;
import com.artiexh.model.rest.cart.CartByArtistResponse;
import com.artiexh.model.rest.cart.CartItemResponse;
import com.artiexh.model.rest.cart.CartResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {CartItemMapper.class}
)
public interface CartMapper {

	Cart entityToDomain(CartEntity cartEntity);

	CartEntity domainToEntity(Cart cart);

	@Mapping(target = "cartByArtists", source = "shoppingCartItems", qualifiedByName = "cartByArtistsMapping")
	CartResponse domainToCartResponse(Cart cart);

	@Named("cartByArtistsMapping")
	default Set<CartByArtistResponse> cartByArtistsMapping(Set<CartItem> shoppingCartItems) {
		return shoppingCartItems.stream()
			.collect(Collectors.groupingBy(
				cartItem -> cartItem.getMerch().getOwner(),
				Collectors.mapping(this::domainToCartItemResponse, Collectors.toSet())
			))
			.entrySet().stream()
			.map(entry -> new CartByArtistResponse(
				artistToArtistCartInfo(entry.getKey()),
				entry.getValue()
			))
			.collect(Collectors.toSet());
	}

	ArtistCartInfoResponse artistToArtistCartInfo(Artist artist);

	@Mapping(target = "id", source = "merch.id")
	@Mapping(target = "status", source = "merch.status")
	@Mapping(target = "currency", source = "merch.currency")
	@Mapping(target = "name", source = "merch.name")
	@Mapping(target = "price", source = "merch.price")
	@Mapping(target = "description", source = "merch.description")
	@Mapping(target = "type", source = "merch.type")
	@Mapping(target = "remainingQuantity", source = "merch.remainingQuantity")
	@Mapping(target = "publishDatetime", source = "merch.publishDatetime")
	@Mapping(target = "maxItemsPerOrder", source = "merch.maxItemsPerOrder")
	@Mapping(target = "deliveryType", source = "merch.deliveryType")
	@Mapping(target = "attaches", source = "merch.attaches")
	CartItemResponse domainToCartItemResponse(CartItem cartItem);

}
