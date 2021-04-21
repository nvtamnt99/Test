package com.shopme.site;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.shopme.common.entity.Currency;
import com.shopme.site.currency.CurrencyRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CurrencyReposistoryTests {

	@Autowired
	private CurrencyRepository repo;
	
	@Test
	public void testLoadAllCurrencies() {
		List<Currency> currencies = repo.findAllByOrderByNameAsc();
		
		for (Currency currency : currencies) {
			System.out.println(currency);
		}
		
		assertThat(currencies).isNotEmpty();
	}
}
