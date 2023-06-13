package com.artiexh.data.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "shopping_cart_item")
public class ShoppingCartItemEntity {

    @EmbeddedId
    private ShoppingCartItemId id;

    @MapsId("merchId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "merch_id", nullable = false)
    private MerchEntity merch;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

}