package com.artiexh.data.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

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
	private Set<ProductInventoryEntity> products;

	@Column(name = "bank_account")
	private String bankAccount;

	@Column(name = "bank_name")
	private String bankName;

	@OneToMany(mappedBy = "artist")
	@ToString.Exclude
	private Set<SubscriptionEntity> subscriptionsFrom;

	@Size(max = 15)
	@Column(name = "shop_phone", length = 15)
	private String phone;

	@Column(name = "shop_thumbnail_url")
	private String shopThumbnailUrl;

	@Column(name = "description")
	private String description;

	@Builder.Default
	@OneToMany(mappedBy = "owner")
	private Set<CampaignEntity> campaigns = new LinkedHashSet<>();

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