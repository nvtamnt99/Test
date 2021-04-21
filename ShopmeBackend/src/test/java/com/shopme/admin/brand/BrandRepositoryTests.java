package com.shopme.admin.brand;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Brand;
import com.shopme.admin.brand.BrandRepository;
import com.shopme.common.entity.Category;
import com.shopme.admin.category.CategoryRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class BrandRepositoryTests {
	
	@Autowired
	private BrandRepository brandRepo;
	
	@Autowired
	private CategoryRepository catRepo;

	@Test
	@Rollback(false)
	public void testCreate() {
		Brand brand = new Brand("Samsung", "samsunglogo.png");
		Brand savedBrand = brandRepo.save(brand);
		
		Assertions.assertTrue(savedBrand.getId() > 0);
	}
	
	@Test
	@Rollback(false)
	public void testCreateWithCategory() {
		Brand brand = new Brand("Apple", "apple.jpg");
		Category category = catRepo.findById(22).get();
		brand.addCategory(category);
		
		Brand savedBrand = brandRepo.save(brand);
		
		Assertions.assertTrue(savedBrand.getId() > 0);
		
		Assertions.assertFalse(savedBrand.getCategories().isEmpty());
		
	}
	
	@Test
	@Rollback(false)
	public void testAddCategoryToExistingBrand() {
		Brand brand = brandRepo.findById(1).get();
		Category category = catRepo.findById(1).get();
		brand.addCategory(category);
		
		Brand savedBrand = brandRepo.save(brand);
		
		Assertions.assertTrue(savedBrand.getId() > 0);
		
		Assertions.assertFalse(savedBrand.getCategories().isEmpty());
		
	}
	
	@Test
	@Rollback(false)
	public void testRemoveCategoryFromBrand() {
		Brand brand = brandRepo.findById(3).get();
		Category category = catRepo.findById(22).get();
		
		Set<Category> categories = brand.getCategories();
		int categoriesBefore = categories.size();
		
		categories.remove(category);
		
		Brand updatedBrand = brandRepo.save(brand);
		categories = updatedBrand.getCategories();
		
		int categoriesAfter = categories.size();

		Assertions.assertEquals(categoriesAfter, categoriesBefore - 1);
	}
	
	@Test
	public void testFindByNameFound() {
		String name = "Honda";
		Brand brand = brandRepo.findByName(name);
		
		assertThat(brand.getName()).isEqualTo(name);
	}
	
	@Test
	public void testFindByNameNotFound() {
		String name = "honda1233";
		Brand brand = brandRepo.findByName(name);
		
		assertThat(brand).isNull();
	}	
}
