package com.artiexh.model.mapper;

import com.artiexh.data.jpa.entity.SubscriptionEntity;
import com.artiexh.model.domain.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {ProductMapper.class, AccountMapper.class, ProductAttachMapper.class}
)
public interface SubscriptionMapper {

    Subscription entityToDomain(SubscriptionEntity subscriptionEntity);

    SubscriptionEntity domainToEntity(Subscription subscription);
}
