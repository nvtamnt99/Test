package com.shopme.admin.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.customer.CustomerRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CustomerRepositoryTestsRealDB {

	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private CustomerRepository repo;
	
	@Test
	public void testCreateCustomer() {
		Integer countryId = 234;
		Country country = entityManager.find(Country.class, countryId);
		
		Customer customer = new Customer("tom@gmail.com", "tommy123", "Tommy Smith", "1800190023", "103 Block A", "North East Building", "Houston", "Texas", "817915", true);
		customer.setCreatedTime(new Date());
		customer.setCountry(country);
		
		Customer savedCustomer = repo.save(customer);
		
		assertThat(savedCustomer).isNotNull();
		assertThat(savedCustomer.getId()).isGreaterThan(0);
	}
}
