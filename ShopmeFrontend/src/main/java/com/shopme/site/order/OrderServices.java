package com.shopme.site.order;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Order;
import com.shopme.common.entity.OrderDetail;
import com.shopme.common.entity.OrderStatus;
import com.shopme.common.entity.OrderTrack;
import com.shopme.common.entity.PaymentMethod;
import com.shopme.common.entity.Product;
import com.shopme.site.shoppingcart.CartItemRepository;

@Service
@Transactional
public class OrderServices {

	public static final int ORDERS_PER_PAGE = 4;
	
	@Autowired
	private OrderRepository orderRepo;
	
	@Autowired
	private CartItemRepository cartRepo;
	
	public Order placeOrder(Customer customer, Address address, List<CartItem> cartItems,
			PaymentMethod paymentMethod, float productTotal, float productCost, float shippingCost, float paymentTotal,
			int deliverDays) {
		Order newOrder = new Order();
		
		newOrder.setCustomer(customer);
		newOrder.setCost(productCost);
		newOrder.setSubtotal(productTotal);
		newOrder.setShippingCost(shippingCost);
		newOrder.setTax(0.0f);
		newOrder.setTotal(paymentTotal);
		newOrder.setOrderTime(new Date());
		newOrder.setStatus(OrderStatus.NEW);		
		newOrder.setPaymentMethod(paymentMethod);
		newOrder.setDeliverDays(deliverDays);
		
		Date deliverDate = calculateDeliverDate(deliverDays);
		newOrder.setDeliverDate(deliverDate);
		
		if (address == null) {
			newOrder.copyShippingAddressFromCustomer(customer);
		} else {
			newOrder.copyShippingAddress(address);
		}

		Set<OrderDetail> orderDetails = newOrder.getOrderDetails();
		
		for (CartItem cartItem : cartItems) {
			Product product = cartItem.getProduct();
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setOrder(newOrder);
			orderDetail.setProduct(product);
			orderDetail.setQuantity(cartItem.getQuantity());
			orderDetail.setUnitPrice(product.getDiscountPrice());
			orderDetail.setCost(product.getCost() * cartItem.getQuantity());
			orderDetail.setSubtotal(cartItem.getSubtotal());
			orderDetail.setShip(cartItem.getShip());

			orderDetails.add(orderDetail);
		}
		
		OrderTrack firstTrack = new OrderTrack();
		firstTrack.setOrder(newOrder);
		firstTrack.setStatus(OrderStatus.NEW);
		firstTrack.setUpdatedTime(new Date());
		firstTrack.setNotes(OrderStatus.NEW.getDescription());
		
		newOrder.getOrderTracks().add(firstTrack);
		
		if (paymentMethod.equals(PaymentMethod.PAYPAL)) {
			OrderTrack paidTrack = new OrderTrack();
			paidTrack.setOrder(newOrder);
			paidTrack.setStatus(OrderStatus.PAID);
			paidTrack.setUpdatedTime(new Date());
			paidTrack.setNotes(OrderStatus.PAID.getDescription());
			
			newOrder.getOrderTracks().add(paidTrack);			
		}
		
		Order savedOrder = orderRepo.save(newOrder);
		cartRepo.deleteByCustomer(customer.getId());
		
		return savedOrder;
	}
	
	public Date calculateDeliverDate(int deliverDays) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, deliverDays);
		
		return calendar.getTime();
	}
	
	public Page<Order> getOrdersForCustomer(Customer customer, int pageNum, 
			String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);
		
		if (keyword != null) {
			return orderRepo.findAll(keyword, customer.getId(), pageable);
		}
		
		return orderRepo.findAll(customer.getId(), pageable);
		
	}
	
	public Order getOrderDetails(Integer id, Customer customer) {
		return orderRepo.findByIdAndCustomer(id, customer);
	}
}
