package com.shopme.site;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.shopme.common.entity.Category;
import com.shopme.site.category.CategoryRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class CategoryTests {

	@Autowired
	private CategoryRepository repo;
	
	@Test
	public void testListRootCategories() {
		List<Category> categories = repo.listRootCategories();
		
		for (Category cat : categories) {
			System.out.println(cat.getName());
		}
		
		assertThat(categories).size().isGreaterThan(0);
	}
	
	@Test
	public void testFindByAlias() {
		String alias = "books";
		Category cat = repo.findByAliasEnabled(alias);
		System.out.println(cat.getName());
		
		assertNotNull(cat);
	}
}
