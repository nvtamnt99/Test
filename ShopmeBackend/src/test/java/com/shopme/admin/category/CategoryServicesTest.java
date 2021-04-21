package com.shopme.admin.category;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.common.entity.Category;
import com.shopme.admin.category.CategoryRepository;
import com.shopme.admin.category.CategoryServices;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServicesTest {

	@MockBean
	private CategoryRepository repo;
	
	@InjectMocks
	private CategoryServices service;
		
	@Test
	public void testCheckUniqueNewModeDuplicatedName() {
		Integer id = null;
		String name = "Electronics";
		String alias = "abcdef";
		
		Category category = new Category(1, name, true, alias, "electronic.png");
		Mockito.when(repo.findByName(name)).thenReturn(category);
		
		Mockito.when(repo.findByAlias(alias)).thenReturn(null);
		
		String result = service.checkUnique(id, name, alias);
		
		assertEquals(result, "DuplicatedName");
	}
	
	@Test
	public void testCheckUniqueNewModeDuplicatedAlias() {
		Integer id = null;
		String name = "Sportive";
		String alias = "sports";
		
		Category category = new Category(1, "Motorbike", true, alias, "sport.png");
		Mockito.when(repo.findByName(name)).thenReturn(null);
		
		Mockito.when(repo.findByAlias(alias)).thenReturn(category);
		
		String result = service.checkUnique(id, name, alias);
		
		assertEquals(result, "DuplicatedAlias");
	}
	
	@Test
	public void testCheckUniqueNewModeOK() {
		Integer id = null;
		String name = "Electronics";
		String alias = "abcdef";
		
		Mockito.when(repo.findByName(name)).thenReturn(null);
		
		Mockito.when(repo.findByAlias(alias)).thenReturn(null);
		
		String result = service.checkUnique(id, name, alias);
		
		assertEquals(result, "OK");
	}
	
	@Test
	public void testCheckUniqueEditModeOK() {
		Integer id = 2;
		String name = "Electronics";
		String alias = "abcdef";
		
		Category category = new Category(2, name, true, alias, "electronic.png");		
		Mockito.when(repo.findByName(name)).thenReturn(category);
		
		Mockito.when(repo.findByAlias(alias)).thenReturn(null);
		
		String result = service.checkUnique(id, name, alias);
		
		assertEquals(result, "OK");
	}
	
	@Test
	public void testCheckUniqueEditModeDuplicatedName() {
		Integer id = 2;
		String name = "Electronics";
		String alias = "abcdef";
		
		Category category = new Category(1, name, true, alias, "electronic.png");		
		Mockito.when(repo.findByName(name)).thenReturn(category);
		
		Mockito.when(repo.findByAlias(alias)).thenReturn(null);
		
		String result = service.checkUnique(id, name, alias);
		
		assertEquals(result, "DuplicatedName");
	}
	
	@Test
	public void testCheckUniqueEditModeDuplicatedAlias() {
		Integer id = 1;
		String name = "Sportive";
		String alias = "sports";
		
		Category category = new Category(2, "Motorbike", true, alias, "sport.png");
		Mockito.when(repo.findByName(name)).thenReturn(null);
		
		Mockito.when(repo.findByAlias(alias)).thenReturn(category);
		
		String result = service.checkUnique(id, name, alias);
		
		assertEquals(result, "DuplicatedAlias");
	}	
}
