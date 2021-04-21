package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Category;
import com.shopme.admin.category.CategoryRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {
	
	@Autowired
	private CategoryRepository repo;
	
	@Test
	public void testCreateNoParent() {
		Category category = new Category("Books", true, null);
		repo.save(category);
	}
	
	@Test
	public void testCreateWithExistingParent() {
		Category parent = new Category(1);
		Category category = new Category("Mobile phones", true, parent);
		repo.save(category);
	}	
	
	@Test
	public void testCreateWithNewParent() {
		Category parent = new Category("Clothing");
		Category category = new Category("Men Jeans", true, parent);
		parent.getChildren().add(category);
		
		repo.save(parent);
	}	
	
	@Test
	public void testGetParent() {
		Category parent = repo.findById(7).get();
		Set<Category> children = parent.getChildren();
		
		for (Category child : children) {
			System.out.println("Child: " + child.getName());
		}
		
		assertThat(parent.getChildren()).size().isEqualTo(1);
	}
	
	@Test
	public void testSearch() {
		String keyword = "phone";
		Pageable pageable = PageRequest.of(0, 5);
		Page<Category> result = repo.search(keyword, pageable);
		List<Category> content = result.getContent();
		
		for (Category cat : content) {
			System.out.println(cat.getName());
		}
		
		assertThat(content).size().isGreaterThan(0);
	}

	@Test
	public void testGetCategoryById() {
		Integer id = 1;
		Category category = repo.findById(id).get();
		Set<Category> children = category.getChildren();
		System.out.println(category.getName());
		
		assertNotNull(category);
		assertThat(children).size().isGreaterThan(0);
	}
	
	@Test
	public void testFindByName() {
		String name = "Electronics";
		Category category = repo.findByName(name);
		
		assertThat(category.getName()).isEqualTo(name);
	}
	
	@Test
	public void testFindByNameNull() {
		String name = "NA";
		Category category = repo.findByName(name);
		
		assertNull(category);
	}	
	
	@Test
	public void testFindByAlias() {
		String alias = "books";
		Category category = repo.findByAlias(alias);
		
		assertThat(category.getAlias()).isEqualTo(alias);
	}	
}
