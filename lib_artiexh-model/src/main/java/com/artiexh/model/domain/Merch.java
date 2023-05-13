package com.artiexh.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Merch {
    private Long id;
    private Artist owner;
    private MerchStatus status;
    private String name;
    private Double price;
    private String description;
    private MerchType type;
    private Long remainingQuantity;
    private LocalDateTime publishDatetime;
    private Long maxItemsPerOrder;
    private DeliveryType deliveryType;
    private Set<String> categories;
    private Set<String> tags;
    private Set<MerchAttach> attaches;
}
