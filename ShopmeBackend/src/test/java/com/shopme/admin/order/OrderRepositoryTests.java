package com.shopme.admin.order;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Order;
import com.shopme.common.entity.OrderDetail;
import com.shopme.common.entity.OrderStatus;
import com.shopme.common.entity.PaymentMethod;
import com.shopme.common.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class OrderRepositoryTests {

	@Autowired
	private OrderRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateOrderSingleProduct() {
		Customer customer = entityManager.find(Customer.class, 1);
		Product canonEOS = entityManager.find(Product.class, 1);
		
		Order mainOrder = new Order();
		mainOrder.setCustomer(customer);
		mainOrder.setPaymentMethod(PaymentMethod.COD);
		mainOrder.copyShippingAddressFromCustomer(customer);
		mainOrder.setOrderTime(new Date());
		mainOrder.setTax(0.0f);
		mainOrder.setShippingCost(2.5f);
		mainOrder.setSubtotal(599.0f);
		mainOrder.setTotal(601.5f);
		
		OrderDetail orderDetail = new OrderDetail();
		orderDetail.setOrder(mainOrder);
		orderDetail.setProduct(canonEOS);
		orderDetail.setQuantity(1);
		orderDetail.setUnitPrice(599.0f);
		orderDetail.setSubtotal(599.0f);
		
		mainOrder.getOrderDetails().add(orderDetail);
		mainOrder.setStatus(OrderStatus.NEW);
		
		repo.save(mainOrder);
	}
	
	@Test
	public void testCreateOrderMultipleProducts() {
		Customer customer = entityManager.find(Customer.class, 5);
		Product hdd1TB = entityManager.find(Product.class, 86);
		Product tpLinkWifi = entityManager.find(Product.class, 105);
		Product sanDisk32GB = entityManager.find(Product.class, 51);
		
		Order mainOrder = new Order();
		mainOrder.setCustomer(customer);
		mainOrder.setPaymentMethod(PaymentMethod.CREDIT_CARD);
		mainOrder.copyShippingAddressFromCustomer(customer);
		mainOrder.setOrderTime(new Date());
		mainOrder.setTax(0.0f);
		mainOrder.setShippingCost(4.5f);
		mainOrder.setSubtotal(112.81f);
		mainOrder.setTotal(117.31f);
		
		OrderDetail orderDetail1 = new OrderDetail();
		orderDetail1.setOrder(mainOrder);
		orderDetail1.setProduct(hdd1TB);
		orderDetail1.setQuantity(1);
		orderDetail1.setUnitPrice(44.84f);
		orderDetail1.setSubtotal(44.84f);

		OrderDetail orderDetail2 = new OrderDetail();
		orderDetail2.setOrder(mainOrder);
		orderDetail2.setProduct(tpLinkWifi);
		orderDetail2.setQuantity(1);
		orderDetail2.setUnitPrice(49.99f);
		orderDetail2.setSubtotal(49.99f);

		OrderDetail orderDetail3 = new OrderDetail();
		orderDetail3.setOrder(mainOrder);
		orderDetail3.setProduct(sanDisk32GB);
		orderDetail3.setQuantity(2);
		orderDetail3.setUnitPrice(8.99f);
		orderDetail3.setSubtotal(17.98f);
		
		mainOrder.getOrderDetails().add(orderDetail1);
		mainOrder.getOrderDetails().add(orderDetail2);
		mainOrder.getOrderDetails().add(orderDetail3);
		
		mainOrder.setStatus(OrderStatus.PROCESSING);
		
		repo.save(mainOrder);
	}	
}
