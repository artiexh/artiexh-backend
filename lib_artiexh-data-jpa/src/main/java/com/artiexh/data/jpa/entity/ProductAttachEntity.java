package com.artiexh.data.jpa.entity;

import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "product_attach")
public class ProductAttachEntity {

    @Id
    @Tsid
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 2048)
    @NotNull
    @Column(name = "url", nullable = false, length = 2048)
    private String url;

    @NotNull
    @Column(name = "type", nullable = false)
    private Byte type;

    @Size(max = 255)
    @Column(name = "title")
    private String title;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

}