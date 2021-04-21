package com.shopme.site;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

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
import com.shopme.site.address.AddressRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class AddressRepositoryTests {

	@Autowired
	private AddressRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateNewAddress() {
		Customer customer = entityManager.find(Customer.class, 5);
		Country usa = entityManager.find(Country.class, 234);
		
		Address address = new Address();
		address.setCustomer(customer);
		address.setCountry(usa);
		address.setFirstName("Charles");
		address.setLastName("Brugger");
		address.setPhoneNumber("646-232-3902");
		address.setAddressLine1("204  Morningview Lane");
		address.setCity("New York");
		address.setState("New York");
		address.setPostalCode("10011");
		
		Address savedAddress = repo.save(address);
		
		assertTrue(savedAddress.getId() > 0);
		
	}

	@Test
	public void testFindByCustomerFound() {
		Customer customer = entityManager.find(Customer.class, 5);
		List<Address> addresses = repo.findByCustomer(customer);
		
		assertTrue(addresses.size() == 1);
	}
	
	@Test
	public void testFindByCustomerNotFound() {
		Customer customer = entityManager.find(Customer.class, 15);
		List<Address> addresses = repo.findByCustomer(customer);
		
		assertTrue(addresses.size() == 0);
	}	
}
