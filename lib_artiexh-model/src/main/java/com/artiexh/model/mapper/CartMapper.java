package com.artiexh.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
	nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
	uses = {ProductMapper.class, AccountMapper.class, ProductAttachMapper.class, ShopMapper.class, ArtistMapper.class, CampaignMapper.class}
)
public interface CartMapper {

	//@Mapping(target = "cartItems", source = "cartItems")
//	Cart entityToDomain(CartEntity cartEntity);
//
//	//@Mapping(target = "product", qualifiedByName = "productEntityToCartItemProduct")
//	CartItem entityToDomain(CartItemEntity cartItemEntity);

	//	@Named("productEntityToCartItemProduct")
//	@Mapping(target = "tags", ignore = true)
//	@Mapping(target = "attaches", ignore = true)
//	@Mapping(target = "owner", qualifiedByName = "basicArtistInfo")
//	@Mapping(target = "shop", qualifiedByName = "basicShopInfo")
//	@Mapping(target = "price.amount", source = "priceAmount")
//	@Mapping(target = "price.unit", source = "priceUnit")
//	@Mapping(target = "thumbnailUrl", source = "attaches", qualifiedByName = "getProductThumbnailUrl")
//	@Mapping(target = "productInCampaign", ignore = true)
//	Product productEntityToCartItemProduct(ProductEntity productEntity);
//
//	@Mapping(target = "id", ignore = true)
//	CartEntity domainToEntity(Cart cart);
//
//	@Mapping(target = "campaigns", source = "cartItems", qualifiedByName = "campaignsResponseMapping")
//	CartResponse domainToCartResponse(Cart cart);
//
//	@Named("campaignsResponseMapping")
//	default Set<CartCampaignResponse> campaignsResponseMapping(Set<CartItem> cartItems) {
//		if (cartItems == null) {
//			return Collections.emptySet();
//		}
//		return cartItems.stream()
//			.collect(Collectors.groupingBy(
//				cartItem -> cartItem.getProduct().getCampaign(),
//				Collectors.mapping(this::domainToCartItemResponse, Collectors.toSet())
//			))
//			.entrySet().stream()
//			.map(this::entryProductByCampaignToCartCampaignResponse)
//			.collect(Collectors.toSet());
//	}
//
//	@Mapping(target = "campaign", source = "entry.key")
//	@Mapping(target = "items", source = "entry.value")
//	CartCampaignResponse entryProductByCampaignToCartCampaignResponse(Map.Entry<Campaign, Set<CartItemResponse>> entry);
//
//	@Mapping(target = "id", source = "product.id")
//	@Mapping(target = "status", source = "product.status")
//	@Mapping(target = "name", source = "product.name")
//	@Mapping(target = "thumbnailUrl", source = "product.thumbnailUrl")
//	@Mapping(target = "price", source = "product.price")
//	@Mapping(target = "description", source = "product.description")
//	@Mapping(target = "type", source = "product.type")
//	@Mapping(target = "remainingQuantity", expression = "java(cartItem.getProduct().getQuantity() - cartItem.getProduct().getSoldQuantity())")
//	@Mapping(target = "maxItemsPerOrder", source = "product.maxItemsPerOrder")
//	@Mapping(target = "deliveryType", source = "product.deliveryType")
//	CartItemResponse domainToCartItemResponse(CartItem cartItem);
//
//	CartItemEntity domainToEntity(CartItem cartItem);

}
