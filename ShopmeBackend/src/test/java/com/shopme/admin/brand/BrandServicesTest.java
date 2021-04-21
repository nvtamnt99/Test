package com.shopme.admin.brand;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.common.entity.Brand;
import com.shopme.admin.brand.BrandRepository;
import com.shopme.admin.brand.BrandServices;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class BrandServicesTest {

	@MockBean
	private BrandRepository repo;
	
	@InjectMocks
	private BrandServices service;
	
	@Test
	public void testCheckUniqueNewModeTrue() {
		Integer id = null;
		String name = "Honda";
		
		when(repo.findByName(name)).thenReturn(null);
		
		boolean isUnique = service.checkUnique(id, name);
		
		assertTrue(isUnique);		
	}
	
	@Test
	public void testCheckUniqueNewModeFalse() {
		Integer id = null;
		String name = "Honda";
		
		when(repo.findByName(name)).thenReturn(new Brand());
		
		boolean isUnique = service.checkUnique(id, name);
		
		assertFalse(isUnique);
	}
	
	@Test
	public void testCheckUniqueEditModeTrue() {
		Integer id = 1;
		String name = "Honda";
		
		when(repo.findByName(name)).thenReturn(new Brand(1, "Honda"));
		
		boolean isUnique = service.checkUnique(id, name);
		
		assertTrue(isUnique);
	}
	
	@Test
	public void testCheckUniqueEditModeFalse() {
		Integer id = 1;
		String name = "Honda";
		
		when(repo.findByName(name)).thenReturn(new Brand(2, "Honda"));
		
		boolean isUnique = service.checkUnique(id, name);
		
		assertFalse(isUnique);
	}	
}
