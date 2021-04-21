package com.shopme.site;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import com.shopme.site.shoppingcart.CartItemRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ShoppingCartTests {

	@Autowired
	private CartItemRepository cartRepo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateCartItem() {
		Product product1 = entityManager.find(Product.class, 1);
		Product product2 = entityManager.find(Product.class, 2);
		Product product3 = entityManager.find(Product.class, 3);
		
		Customer customer = entityManager.find(Customer.class, 1);
		
		CartItem item1 = new CartItem();
		item1.setCustomer(customer);
		item1.setProduct(product1);
		item1.setQuantity(1);
		
		CartItem item2 = new CartItem();
		item2.setCustomer(customer);
		item2.setProduct(product2);
		item2.setQuantity(2);		
		
		CartItem item3 = new CartItem();
		item3.setCustomer(customer);
		item3.setProduct(product3);
		item3.setQuantity(3);		

		List<CartItem> items = Arrays.asList(item1, item2, item3);
		
		cartRepo.saveAll(items);
	}
	
	@Test
	public void testGetItemsByCustomer() {
		Customer customer = new Customer();
		customer.setId(1);
		
		List<CartItem> items = cartRepo.findByCustomer(customer);
		
		assertTrue(items.size() == 3);
	}
}
