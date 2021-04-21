package com.shopme.site;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.shopme.common.entity.OrderStatus;
import com.shopme.site.order.OrderDetailRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class OrderDetailRepositoryTests {

	@Autowired
	private OrderDetailRepository repo;
	
	@Test
	public void testCountByProductAndCustomerAndOrderStatus() {
		Integer productId = 39;
		Integer customerId = 5;
		
		Long count = repo.countByProductAndCustomerAndOrderStatus(
								productId, customerId, OrderStatus.DELIVERED);
		
		System.out.println("Count: " + count);
		
		assertTrue(count > 0);
	}
}
