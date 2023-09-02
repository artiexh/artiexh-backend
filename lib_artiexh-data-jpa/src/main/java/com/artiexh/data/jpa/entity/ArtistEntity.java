package com.artiexh.data.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "artist")
public class ArtistEntity extends UserEntity {

	@OneToMany(mappedBy = "owner")
	@ToString.Exclude
	private Set<ProductEntity> products;

	@OneToMany(mappedBy = "shop")
	@ToString.Exclude
	private Set<ProductEntity> shopProducts;

	@Column(name = "shop_name")
	private String shopName;

	@Column(name = "shop_image_url")
	private String shopImageUrl;

	@OneToMany(mappedBy = "artist")
	@ToString.Exclude
	private Set<SubscriptionEntity> subscriptionsFrom;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "shop_ward_id")
	private WardEntity shopWard;

	@Column(name = "shop_address")
	private String shopAddress;

	@Column(name = "province_id")
	private Integer provinceId;

	@Size(max = 15)
	@Column(name = "shop_phone", length = 15)
	private String shopPhone;

	@Override
	public final boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null) {
			return false;
		}
		Class<?> oEffectiveClass =
			o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
				: o.getClass();
		Class<?> thisEffectiveClass =
			this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
				: this.getClass();
		if (thisEffectiveClass != oEffectiveClass) {
			return false;
		}
		ArtistEntity that = (ArtistEntity) o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer()
			.getPersistentClass().hashCode() : getClass().hashCode();
	}
}