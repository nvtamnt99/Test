package com.shopme.admin.country;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import com.shopme.admin.country.StateRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;

@DataJpaTest(showSql = false)
@TestPropertySource(locations = "classpath:test.properties")
public class StateRepositoryTests {

	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private StateRepository repo;
	
	@Test
	public void testCreateState() {
		Country country = entityManager.persist(new Country("United States"));
		
		State state = repo.save(new State("California", country));
		
		assertNotNull(state);
		assertTrue(state.getId() > 0);
	}
	
	@Test
	public void testListByCountry() {
		Country country1 = entityManager.persist(new Country("United States"));
		entityManager.persist(new State("California", country1));
		entityManager.persist(new State("New York", country1));
		entityManager.persist(new State("Nevada", country1));
		
		Country country2 = entityManager.persist(new Country("United Kingdom"));
		entityManager.persist(new State("London", country2));
		
		Country usa = new Country();
		usa.setId(2);
		
		List<State> listStates = repo.findByCountryOrderByNameAsc(usa);
		
		for (State state : listStates) {
			System.out.println(state.getName());
		}
		
		assertThat(listStates).isNotEmpty();
	}
	
}
