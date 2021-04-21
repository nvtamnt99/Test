package com.shopme.admin.country;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.country.CountryRepository;
import com.shopme.common.entity.Country;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CountryListInsertTest {

	@Autowired
	private CountryRepository repo;
	
	@Test
	public void insertCountryListIntoRealDatabase() {
        String[] countryCodes = Locale.getISOCountries();
        List<Country> listCountries = new ArrayList<>();
         
        for (String countryCode : countryCodes) {
            Locale locale = new Locale("", countryCode);
            String name = locale.getDisplayCountry();
            listCountries.add(new Country(name));
        }		
        
        repo.saveAll(listCountries);
	}
	
	@Test
	public void insertCountryListIntoRealDatabaseWithCountryCode() {
        String[] countryCodes = Locale.getISOCountries();
        List<Country> listCountries = new ArrayList<>();
         
        for (String countryCode : countryCodes) {
            Locale locale = new Locale("", countryCode);
            String name = locale.getDisplayCountry();
            listCountries.add(new Country(name, countryCode));
        }		
        
        repo.saveAll(listCountries);
	}	
	
	@Test
	public void testListCountriesOrderByNameAsc() {
		List<Country> listCountries = repo.findAllByOrderByNameAsc();
		for (Country country : listCountries) {
			System.out.println(country.getName());
		}
	}
}
