package com.shopme.admin.order;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
	@Query("SELECT NEW com.shopme.common.entity.OrderDetail(od.product.category.name, od.subtotal, od.cost, od.ship, od.quantity)"
			+ " FROM OrderDetail od WHERE od.order.orderTime BETWEEN ?1 and ?2")
	public List<OrderDetail> findByOrderDetailCategoryNameTimeBetween(Date startTime, Date endTime);
	
	@Query("SELECT NEW com.shopme.common.entity.OrderDetail(od.subtotal, od.cost, od.ship, od.product.name, od.quantity)"
			+ " FROM OrderDetail od WHERE od.order.orderTime BETWEEN ?1 and ?2")
	public List<OrderDetail> findByOrderDetailProductNameTimeBetween(Date startTime, Date endTime);	
}
