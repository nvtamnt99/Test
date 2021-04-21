package com.shopme.admin.menu;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Article;
import com.shopme.common.entity.Menu;
import com.shopme.common.entity.MenuType;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class MenuRepositoryTests {

	@Autowired
	private MenuRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateHeaderMenu() {
		Menu menu = new Menu();
		menu.setType(MenuType.HEADER);
		menu.setTitle("Shopping Guidelines");
		menu.setAlias("shopping-guide");
		menu.setEnabled(true);
		menu.setPosition(1);
		
		Integer articleId = 7;
		Article article = entityManager.find(Article.class, articleId);
		menu.setArticle(article);
		
		Menu savedMenu = repo.save(menu);
		
		assertTrue(savedMenu.getId() > 0);
	}
	
	@Test
	public void testCreateFooterMenu() {
		Menu menu = new Menu();
		menu.setType(MenuType.FOOTER);
		menu.setTitle("Privacy Policy");
		menu.setAlias("privacy");
		menu.setEnabled(false);
		menu.setPosition(2);
		
		Integer articleId = 3;
		Article article = entityManager.find(Article.class, articleId);
		menu.setArticle(article);
		
		Menu savedMenu = repo.save(menu);
		
		assertTrue(savedMenu.getId() > 0);
	}
	
	@Test
	public void testFindHeaderMenu() {
		List<Menu> headerMenus = repo.findByType(MenuType.HEADER);
		assertThat(headerMenus).size().isEqualTo(1);
	}
	
	@Test
	public void testFindFooterMenu() {
		List<Menu> headerMenus = repo.findByType(MenuType.FOOTER);
		assertThat(headerMenus).size().isEqualTo(3);
	}
	
	@Test
	public void testCountFooterMenus() {
		Long numberOfFooterMenus = repo.countByType(MenuType.FOOTER);
		assertEquals(3, numberOfFooterMenus);
	}
}
