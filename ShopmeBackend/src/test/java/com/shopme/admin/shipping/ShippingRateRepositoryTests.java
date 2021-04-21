package com.shopme.admin.shipping;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ShippingRateRepositoryTests {

	@Autowired
	private ShippingRateRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateNew() {
		Country usa = entityManager.find(Country.class, 234);
		ShippingRate newYorkRate = new ShippingRate();
		
		newYorkRate.setCountry(usa);
		newYorkRate.setState("New York");
		newYorkRate.setDays(7);
		newYorkRate.setRate(10.00f);
		
		ShippingRate savedRate = repo.save(newYorkRate);
		
		assertTrue(savedRate.getId() > 0);
	}
	
	@Test
	public void testFindByCountryAndStateFound() {
		Integer countryId = 234;
		String state = "New York";
		ShippingRate rate = repo.findByCountryAndState(countryId, state);
		
		assertTrue(rate.getId() > 0);
	}
	
	@Test
	public void testFindByCountryAndStateNotFound() {
		Integer countryId = 123;
		String state = "New York";
		ShippingRate rate = repo.findByCountryAndState(countryId, state);
		
		assertNull(rate);
	}	
}
