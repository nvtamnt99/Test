package com.shopme.site;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Product;
import com.shopme.site.product.ProductRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository repo;
	
	@Test
	public void testListByCategory() {
//		Integer categoryId = 22;
//		String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
//		List<Product> products = repo.listByCategory(categoryId, categoryIdMatch);
//		
//		for (Product product : products) {
//			System.out.println(product.getName());
//			String allparentIds = product.getCategory().getAllParentIds();
//			
//			if (allparentIds != null) {
//				assertThat(allparentIds).contains(categoryIdMatch);
//			} else {
//				assertEquals(categoryId, product.getCategory().getId());
//			}
//		}
	}
	
	@Test
	public void testUpdateAverageRating() {
		Integer productId = 45;
		repo.updateAverageRating(productId);
	}
}
