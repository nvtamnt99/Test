package com.shopme.site.shoppingcart;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
	public List<CartItem> findByCustomer(Customer customer);
	
	public CartItem findByCustomerAndProduct(Customer customer, Product product);
	
	@Query("DELETE FROM CartItem c WHERE c.customer.id = ?1 AND c.product.id = ?2")
	@Modifying
	public void deleteByCustomerAndProduct(Integer customerId, Integer productId);
	
	@Query("UPDATE CartItem c SET c.quantity = ?1 WHERE c.product.id = ?2 AND c.customer.id = ?3")
	@Modifying
	public void updateQuantity(Integer quantity, Integer productId, Integer customerId);
	
	@Modifying
	@Query("DELETE CartItem c WHERE c.customer.id = ?1")
	public void deleteByCustomer(Integer customerId);
}
