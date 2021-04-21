package com.shopme.common.entity;

import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;

public class EntityFactory {

	public static User randomUser() {
		String email = RandomStringUtils.random(6).concat("@gmail.com");
		User user = new User();
		user.setEmail(email);
		user.setFirstName("First Name");
		user.setLastName("Last Name");
		user.setEnabled(true);
		user.setPassword("password");
		
		return user;
	}
	
	public static Customer randomCustomer() {
		String email = RandomStringUtils.random(7).concat("@gmail.com");
		Customer customer = new Customer();
		customer.setEmail(email);
		customer.setFirstName("First Name");
		customer.setLastName("Last Name");
		customer.setPassword("password");
		customer.setAddressLine1("Address Line 1");
		customer.setAddressLine2("Address Line 2");
		customer.setCity("Hanoi");
		customer.setCreatedTime(new Date());
		customer.setPhoneNumber("0123456789");
		customer.setPostalCode("100000");
		customer.setEnabled(true);
		customer.setCountry(new Country("Vietnam"));
		
		return customer;		
	}
	
	public static Product randomProduct() {
		String name = RandomStringUtils.random(30);
		Product product = new Product();
		product.setName(name);
		product.setCreatedTime(new Date());
		product.setShortDescription("Short Description");
		
		
		return product;
	}
}
