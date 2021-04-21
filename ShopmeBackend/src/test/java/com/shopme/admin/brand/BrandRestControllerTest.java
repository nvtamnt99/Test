package com.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.shopme.common.entity.Brand;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopme.admin.brand.BrandRepository;
import com.shopme.admin.brand.BrandRestController;
import com.shopme.admin.brand.BrandServices;
import com.shopme.common.entity.Category;
import com.shopme.admin.user.UserRepository;

@WebMvcTest(BrandRestController.class)
public class BrandRestControllerTest {
	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean	
	private BrandServices service;
	
	@MockBean
	private BrandRepository repo;
	
	@MockBean
	private UserRepository userRepo;
	
	@Test
	public void testListCategoriesByBrand() throws Exception {
		Integer id = 1;
		Brand brand = new Brand(id, "Honda", "honda.png");
		brand.addCategory(new Category(1, "Automobile", true, "auto", "automobile.png"));
		brand.addCategory(new Category(2, "Motorbike", true, "motor", "motorbike.png"));
		
		List<CategoryDTO> listCategories = new ArrayList<>();
		CategoryDTO automobile = new CategoryDTO(1, "Automobile");
		CategoryDTO motorbike = new CategoryDTO(2, "Motorbike");
		
		listCategories.add(automobile);
		listCategories.add(motorbike);
		
		
		SecurityMockMvcConfigurers.springSecurity();
		
		when(service.get(id)).thenReturn(brand);
		
		MvcResult mvcResult = mvc.perform(get("/brands/" + id + "/categories"))
			.andExpect(status().isOk()).andReturn();
		String actualJsonResponse = mvcResult.getResponse().getContentAsString();
		
		String expectedJsonResponse = objectMapper.writeValueAsString(listCategories);
		assertThat(actualJsonResponse).isEqualToIgnoringWhitespace(expectedJsonResponse);
		
		verify(service, times(1)).get(id);
	}
}
