package com.shopme.admin.product;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.common.entity.Product;
import com.shopme.admin.product.ProductRepository;
import com.shopme.admin.product.ProductServices;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class ProductServicesTest {

	@MockBean
	private ProductRepository repo;
	
	@InjectMocks
	private ProductServices service;
	
	@Test
	public void testCheckUniqueNewModeTrue() {
		Integer id = null;
		String name = "iPhone 8s";
		
		when(repo.findByName(name)).thenReturn(null);
		
		boolean isUnique = service.checkUnique(id, name);
		
		assertTrue(isUnique);
	}
	
	@Test
	public void testCheckUniqueNewModeFalse() {
		Integer id = null;
		String name = "iPhone 8s";
		
		when(repo.findByName(name)).thenReturn(new Product(1, "iPhone 8s"));
		
		boolean isUnique = service.checkUnique(id, name);
		
		assertFalse(isUnique);
	}
	
	@Test
	public void testCheckUniqueEditModeTrue() {
		Integer id = 1;
		String name = "iPhone 8s";
		
		when(repo.findByName(name)).thenReturn(new Product(1, "iPhone 8s"));
		
		boolean isUnique = service.checkUnique(id, name);
		
		assertTrue(isUnique);
	}
	
	@Test
	public void testCheckUniqueEditModeFalse() {
		Integer id = 1;
		String name = "iPhone 8s";
		
		when(repo.findByName(name)).thenReturn(new Product(2, "iPhone 8s"));
		
		boolean isUnique = service.checkUnique(id, name);
		
		assertFalse(isUnique);
	}	
}
