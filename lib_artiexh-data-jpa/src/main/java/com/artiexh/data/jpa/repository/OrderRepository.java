package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.OrderEntity;
import com.artiexh.data.jpa.projection.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {
	@Query(nativeQuery = true,
	value = """
SELECT sum(p.price_amount * od.quantity) as priceAmount, p.price_unit as priceUnit, o.user_id as ownerId, o.status as status
from `order` o
inner join order_detail od on o.id = od.order_id
inner join product p on od.product_id = p.id
where o.id = :id
group by o.id
""")
	Bill getBillInfo(@Param("id") Long id);

	Optional<OrderEntity> findByIdAndShopId(Long orderId, Long artistId);

}
