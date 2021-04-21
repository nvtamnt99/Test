package com.shopme.admin.country;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import com.shopme.admin.country.CountryRepository;
import com.shopme.common.entity.Country;

@DataJpaTest
@TestPropertySource(locations = "classpath:test.properties")
public class CountryRepositoryTests {
	
	@Autowired
	private CountryRepository repo;
	
	@Test
	public void testCreateCountry() {
		Country country = repo.save(new Country("United States"));
		
		assertNotNull(country);
		assertTrue(country.getId() > 0);
	}
}
