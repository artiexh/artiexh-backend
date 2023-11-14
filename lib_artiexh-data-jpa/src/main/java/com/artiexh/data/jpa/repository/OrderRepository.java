package com.artiexh.data.jpa.repository;

import com.artiexh.data.jpa.entity.OrderEntity;
import com.artiexh.data.jpa.projection.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long>, JpaSpecificationExecutor<OrderEntity> {
	@Query(nativeQuery = true,
		value = """
			SELECT co.id as orderId, sum(p.price_amount * od.quantity) + co.shipping_fee as orderAmount, p.price_unit as priceUnit, o.user_id as ownerId, co.status as status
			from `order` o
			inner join campaign_order co on o.id = co.order_id
			inner join order_detail od on co.id = od.campaign_order_id
			inner join product p on od.campaign_sale_id = p.campaign_sale_id and od.product_code = p.product_code
			where o.id = :id
			group by co.id""")
	List<Bill> getBillInfo(@Param("id") Long id);


}
