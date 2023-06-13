package com.artiexh.model.rest.cart;

import com.artiexh.model.domain.DeliveryType;
import com.artiexh.model.domain.MerchAttach;
import com.artiexh.model.domain.MerchStatus;
import com.artiexh.model.domain.MerchType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private Long id;
    private MerchStatus status;
    private String currency;
    private String name;
    private BigDecimal price;
    private String description;
    private MerchType type;
    private Long remainingQuantity;
    private Instant publishDatetime;
    private Long maxItemsPerOrder;
    private DeliveryType deliveryType;
    private Integer quantity;
    private Set<MerchAttach> attaches;
}
