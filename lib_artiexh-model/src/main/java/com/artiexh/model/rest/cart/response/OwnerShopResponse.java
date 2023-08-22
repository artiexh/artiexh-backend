package com.artiexh.model.rest.cart.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnerShopResponse {
    private String id;
    private String name;
    private OwnerShopResponse ownerShop;
}
