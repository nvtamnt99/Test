package com.shopme.site.order;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
	public List<Order> findByCustomerOrderByOrderTimeDesc(Customer customer);
	
	public Order findByIdAndCustomer(Integer id, Customer customer);
	
	@Query("SELECT o FROM Order o WHERE o.customer.id = ?2 AND (o.firstName LIKE %?1% OR"
			+ " o.lastName LIKE %?1% OR o.phoneNumber LIKE %?1% OR"
			+ " o.addressLine1 LIKE %?1% OR o.addressLine2 LIKE %?1% OR"
			+ " o.postalCode LIKE %?1% OR o.city LIKE %?1% OR"
			+ " o.state LIKE %?1% OR o.country LIKE %?1% OR"
			+ " o.paymentMethod LIKE %?1% OR o.status LIKE %?1%)")
	public Page<Order> findAll(String keyword, Integer customerId, Pageable pageable);
	
	@Query("SELECT o FROM Order o WHERE o.customer.id = ?1")
	public Page<Order> findAll(Integer customerId, Pageable pageable);
}
