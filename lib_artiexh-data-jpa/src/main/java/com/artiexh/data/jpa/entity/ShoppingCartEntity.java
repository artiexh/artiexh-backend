package com.artiexh.data.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "shopping_cart")
public class ShoppingCartEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long id;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "cart_id")
    private Set<ShoppingCartItemEntity> shoppingCartItemEntities = new LinkedHashSet<>();

}