package com.shopme.admin.order;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Order;

@Service
public class OrderServices {

	public static final int ORDERS_PER_PAGE = 10;
	
	@Autowired
	private OrderRepository repo;
	
	public List<Order> listAll() {
		return repo.findAllByOrderByOrderTimeDesc();
	}
	
	public Page<Order> listAll(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);
		
		if (keyword != null) {
			return repo.findAll(keyword, pageable);
		}
		
		return repo.findAll(pageable);
	}	
	
	public Order get(Integer id) throws OrderNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new OrderNotFoundException("Could not find any orders with ID " + id);
		}
	}
	
	public void delete(Integer id) throws OrderNotFoundException {
		Long count = repo.countById(id);
		if (count == null || count == 0) {
			throw new OrderNotFoundException("Could not find any orders with ID " + id); 
		}
		
		repo.deleteById(id);
	}
	
	public void save(Order orderInForm) {
		Order orderInDB = repo.findById(orderInForm.getId()).get();
		updateOrderOverviewInfo(orderInForm, orderInDB);
		updateOrderShippingInfo(orderInForm, orderInDB);
		updateOrderProducts(orderInForm, orderInDB);
		updateOrderTracks(orderInForm, orderInDB);
		
		repo.save(orderInDB);
	}
	
	private void updateOrderOverviewInfo(Order orderInForm, Order orderInDB) {
		orderInDB.setSubtotal(orderInForm.getSubtotal());
		orderInDB.setShippingCost(orderInForm.getShippingCost());
		orderInDB.setTax(orderInForm.getTax());
		orderInDB.setTotal(orderInForm.getTotal());
		orderInDB.setCost(orderInForm.getCost());
		orderInDB.setPaymentMethod(orderInForm.getPaymentMethod());
		orderInDB.setStatus(orderInForm.getStatus());
	}
	
	private void updateOrderShippingInfo(Order orderInForm, Order orderInDB) {
		orderInDB.setDeliverDays(orderInForm.getDeliverDays());
		orderInDB.setDeliverDate(orderInForm.getDeliverDate());
		orderInDB.setFirstName(orderInForm.getFirstName());
		orderInDB.setLastName(orderInForm.getLastName());
		orderInDB.setPhoneNumber(orderInForm.getPhoneNumber());
		orderInDB.setAddressLine1(orderInForm.getAddressLine1());
		orderInDB.setAddressLine2(orderInForm.getAddressLine2());
		orderInDB.setCity(orderInForm.getCity());
		orderInDB.setState(orderInForm.getState());
		orderInDB.setCountry(orderInForm.getCountry());
		orderInDB.setPostalCode(orderInForm.getPostalCode());
	}
	
	private void updateOrderProducts(Order orderInForm, Order orderInDB) {
		orderInDB.getOrderDetails().clear();
		orderInDB.getOrderDetails().addAll(orderInForm.getOrderDetails());
	}
	
	private void updateOrderTracks(Order orderInForm, Order orderInDB) {
		orderInDB.getOrderTracks().clear();
		orderInDB.getOrderTracks().addAll(orderInForm.getOrderTracks());
	}
}
