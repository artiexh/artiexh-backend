package com.artiexh.data.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.Set;

@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Getter
@Setter
@ToString
@RequiredArgsConstructor
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

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "province_id")
	private ProvinceEntity province;

	@Override
	public final boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
		Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
		if (thisEffectiveClass != oEffectiveClass) return false;
		ArtistEntity that = (ArtistEntity) o;
		return getId() != null && Objects.equals(getId(), that.getId());
	}

	@Override
	public final int hashCode() {
		return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
	}
}