package com.shopme.admin.country;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.admin.user.UserRepository;
import com.shopme.common.entity.Country;

@WebMvcTest(CountryRestController.class)
public class CountryRestControllerTests {

	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private CountryRepository repo;
	
	@MockBean
	private UserRepository userRepo;	
	
	@Test
	public void testCreateCountry() throws Exception {
		Country newCountry = new Country("India");
		Country savedCountry = new Country(1, "India");
		
		when(repo.save(newCountry)).thenReturn(savedCountry);
		
		String url = "/countries/save";
		
		mockMvc.perform(
				post(url).contentType("application/json")
						 .content(objectMapper.writeValueAsString(newCountry))
						 .with(csrf()))
			.andExpect(status().isOk())
			.andExpect(content().string("1"))
		;
		
		verify(repo, times(1)).save(newCountry);
	}
	
	@Test
	public void testUpdateCountry() throws JsonProcessingException, Exception {
		Country country = new Country(2, "United States");
		
		Mockito.when(repo.save(country)).thenReturn(country);
		
		String url = "/countries/save";
		
		mockMvc.perform(
				post(url).contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(country))
				.with(csrf())
				)
			.andExpect(status().isOk())
			.andExpect(content().string("2"))
		;
	}
	
	@Test
	public void testListCountries() throws Exception {
		List<Country> listCountries = new ArrayList<>();
		listCountries.add(new Country(1, "Angola"));
		listCountries.add(new Country(2, "Belgium"));
		listCountries.add(new Country(3, "Canada"));
		listCountries.add(new Country(4, "Denmark"));
		listCountries.add(new Country(5, "England"));
		
		when(repo.findAllByOrderByNameAsc()).thenReturn(listCountries);
		
		String url = "/countries/list";
		
		MvcResult mvcResult = mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();
		
		String actualJsonResponse = mvcResult.getResponse().getContentAsString();
		
		String expectedJsonResponse = objectMapper.writeValueAsString(listCountries);
		
		assertThat(actualJsonResponse).isEqualToIgnoringWhitespace(expectedJsonResponse);
	}
	
	@Test
	public void testDeleteCountry() throws Exception {
		Integer countryId = 1;
		doNothing().when(repo).deleteById(countryId);
		
		String url = "/countries/delete/" + countryId;
		
		mockMvc.perform(get(URI.create(url))).andExpect(status().isOk());
		
		verify(repo, times(1)).deleteById(countryId);
		
	}
	
	@Test
	public void testCountryNameMustNotBeBlank() throws Exception {
		Country newCountry = new Country("");
		
		String url = "/countries/save";
		
		mockMvc.perform(
				post(url).contentType("application/json")
						 .content(objectMapper.writeValueAsString(newCountry))
						 .with(csrf()))
			.andExpect(status().isBadRequest())
		;
		
		verify(repo, times(0)).save(newCountry);
	}	
	
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
		
		verify(repo, times(0)).save(newCountry);
	}	
}
