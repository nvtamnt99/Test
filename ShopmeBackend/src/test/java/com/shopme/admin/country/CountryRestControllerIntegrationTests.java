package com.shopme.admin.country;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.common.entity.Country;

@SpringBootTest
@AutoConfigureMockMvc
public class CountryRestControllerIntegrationTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private CountryRepository repo;
	
	@Test
	public void testCountryNameMustNotBeNull() throws Exception {
		Country newCountry = new Country(1, null);
		
		String url = "/countries/save";
		
		mockMvc.perform(
				post(url).contentType("application/json")
						 .content(objectMapper.writeValueAsString(newCountry))
						 .with(csrf()))
			.andExpect(status().isBadRequest())
		;
	}		
	
	@Test
	public void testCreateCountry() throws Exception {
		String countryName = "United Kingdom of India";
		Country newCountry = new Country(countryName);
		
		String url = "/countries/save";
		
		MvcResult mvcResult = mockMvc.perform(
				post(url).contentType("application/json")
						 .content(objectMapper.writeValueAsString(newCountry))
						 .with(csrf()))
			.andExpect(status().isOk()).andReturn();
		
		String response = mvcResult.getResponse().getContentAsString();
		Integer countryId = new Integer(response);
		
		Country savedCountry = repo.findById(countryId).get();
		
		assertThat(savedCountry.getName()).isEqualTo(countryName);
		
	}
	
	@Test
	public void testUpdateCountry() throws Exception {
		Integer countryId = 266;
		String countryName = "United Himalaya";
		
		Country newCountry = new Country(countryId, countryName);
		
		String url = "/countries/save";
		
		mockMvc.perform(
				post(url).contentType("application/json")
						 .content(objectMapper.writeValueAsString(newCountry))
						 .with(csrf()))
			.andExpect(status().isOk())
			.andExpect(content().string(String.valueOf(countryId)))
			.andReturn();
		
		Country savedCountry = repo.findById(countryId).get();
		
		assertThat(savedCountry.getName()).isEqualTo(countryName);
		
	}
	
	@Test
	public void testDeleteCountry() throws Exception {
		Integer countryId = 257;
		
		String url = "/countries/delete/" + countryId;
		
		mockMvc.perform(get(URI.create(url))).andExpect(status().isOk());
		
		Optional<Country> result = repo.findById(countryId);
		
		assertThat(result).isNotPresent();
	}
	
	@Test
	public void testListCountries() throws Exception {
		
		String url = "/countries/list";
		
		MvcResult mvcResult = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();
		
		String jsonResponse = mvcResult.getResponse().getContentAsString();
		Country[] countries = objectMapper.readValue(jsonResponse, Country[].class);
		
		assertThat(countries).hasSizeGreaterThan(200);

	}	
}
