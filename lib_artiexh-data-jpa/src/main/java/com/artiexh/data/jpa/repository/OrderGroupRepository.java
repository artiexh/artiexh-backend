package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.OrderGroupEntity;
import com.artiexh.data.jpa.projection.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderGroupRepository extends JpaRepository<OrderGroupEntity, Long> {
	@Query(nativeQuery = true,
		value = """
		SELECT o.id as orderId, sum(p.price_amount * od.quantity) + o.shipping_fee as orderAmount, p.price_unit as priceUnit, og.user_id as ownerId, o.status as status
		from order_group og
		inner join `order` o on og.id = o.order_group_id
		inner join order_detail od on o.id = od.order_id
		inner join product p on od.product_id = p.id
		where og.id = :id
		group by o.id""")
	List<Bill> getBillInfo(@Param("id") Long id);


}
