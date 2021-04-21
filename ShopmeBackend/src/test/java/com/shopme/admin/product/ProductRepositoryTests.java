package com.shopme.admin.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Date;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import com.shopme.admin.product.ProductRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateProduct() {
		
		Brand brand = entityManager.find(Brand.class, 1);
		Category category = entityManager.find(Category.class, 1);
		
		Product product = new Product();
		product.setName("Samsung Galaxy A31");
		product.setShortDescription("Latest smartphone from Samsung");
		product.setFullDescription("This is a great smartphone ever");
		product.setInStock(true);
		product.setEnabled(true);
		product.setBrand(brand);
		product.setCategory(category);
		product.setMainImage("galaxya31.png");
		product.setCreatedTime(new Date());
		product.setUpdatedTime(new Date());
		
		Product savedProduct = repo.save(product);
		
		Assertions.assertTrue(savedProduct.getId() > 0);
	}
	
	@Test
	public void testSaveProductWithMultipleImages() {
		Product product = repo.findById(1).get();
		product.addExtraImage("image1.png");
		product.addExtraImage("image2.png");
		product.addExtraImage("image3.png");
		product.addExtraImage("image4.png");
		
		
		Product savedProduct = repo.save(product);
		
		Assertions.assertTrue(savedProduct.getId() > 0);		
	}
	
	@Test
	public void testUpdateProductForDetails() {
		Product product = repo.findById(2).get();
		product.addDetail("Product Dimensions", "7 x 5 x 4 inches");
		product.addDetail("Item Weight", "0.43 ounces");
		product.addDetail("Operating System", "IOS");
		product.addDetail("Computer Memory Size", "64 GB");
		
		Product savedProduct = repo.save(product);
		
		assertThat(savedProduct.getDetails()).isNotEmpty();
	}
	
	@Test
	public void testFindByNameFound() {
		String name = "Samsung Galaxy A31";
		Product product = repo.findByName(name);
		
		assertThat(product.getName()).isEqualTo(name);
	}
	
	@Test
	public void testFindByNameNotFound() {
		String name = "Samsung Galaxy A3123";
		Product product = repo.findByName(name);
		
		assertNull(product);
	}	
}
