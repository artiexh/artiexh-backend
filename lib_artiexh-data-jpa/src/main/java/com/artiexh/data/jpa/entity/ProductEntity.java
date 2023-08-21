package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;

@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "product")
@Inheritance(strategy = InheritanceType.JOINED)
public class ProductEntity {

    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "owner_id", nullable = false)
    private ArtistEntity owner;

    @NotNull
    @Column(name = "status", nullable = false)
    private Byte status;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "price_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal priceAmount;

    @Size(max = 3)
    @NotNull
    @Column(name = "price_unit", nullable = false, length = 3)
    private String priceUnit;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private ProductCategoryEntity category;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "type", nullable = false)
    private Byte type;

    @NotNull
    @Column(name = "remaining_quantity", nullable = false)
    private Integer remainingQuantity;

    @Column(name = "publish_datetime")
    private Instant publishDatetime;

    @Column(name = "max_items_per_order")
    private Integer maxItemsPerOrder;

    @NotNull
    @Column(name = "delivery_type", nullable = false)
    private Byte deliveryType;

    @NotNull
    @Column(name = "average_rate", nullable = false)
    private Float averageRate;

    @NotNull
    @Column(name = "payment_method", nullable = false)
    private byte[] paymentMethod;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "shop_id", nullable = false)
    private ArtistEntity shop;

    @OneToMany(mappedBy = "product")
    private Set<ProductAttachEntity> productAttaches = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "products")
    private Set<ProductTagEntity> productTags = new LinkedHashSet<>();

}