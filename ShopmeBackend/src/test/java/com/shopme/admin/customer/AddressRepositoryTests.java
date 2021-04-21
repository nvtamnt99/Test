package com.shopme.admin.customer;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class AddressRepositoryTests {
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private AddressRepository repo;
	
	@Test
	public void testCreateAddress() {
		Integer customerId = 1;
		Integer countryId = 242;
		Customer customer = entityManager.find(Customer.class, customerId);
		
		Country country = entityManager.find(Country.class, countryId);
		
		Address newAddress = new Address();
		newAddress.setCountry(country);
		newAddress.setCustomer(customer);
		newAddress.setAddressLine1("Address Line 1");
		newAddress.setAddressLine2("Address Line 2");
		newAddress.setFirstName("John");
		newAddress.setLastName("Doe");
		newAddress.setPhoneNumber("123456789");
		newAddress.setCity("Hanoi");
		newAddress.setPostalCode("100000");
		
		Address savedAddress = repo.save(newAddress);
		
		assertTrue(savedAddress.getId() > 0);
	}
}
