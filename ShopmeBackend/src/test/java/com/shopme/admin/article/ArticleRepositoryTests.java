package com.shopme.admin.article;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Article;
import com.shopme.common.entity.ArticleType;
import com.shopme.common.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ArticleRepositoryTests {

	@Autowired
	private ArticleRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateNewMenuBoundArticle() {
		Article article = new Article();
		article.setTitle("About Us");
		article.setContent("About Shopme E-commerce Ltd. We're a big ecommerce website in the world");
		article.setAlias("about");
		article.setPublished(true);
		article.setType(ArticleType.MENU_BOUND);
		article.setUpdatedTime(new Date());
		
		Integer userId = 6;
		User user = entityManager.find(User.class, userId);
		
		article.setUser(user);
		
		Article savedArticle = repo.save(article);
		
		assertTrue(savedArticle.getId() > 0);
	}
	
	@Test
	public void testCreateFreerticle() {
		Article article = new Article();
		article.setTitle("Deal in October 2020");
		article.setContent("Great deals across the site in October 2020. Here are the details: .....");
		article.setAlias("deal-oct-2020");
		article.setPublished(false);
		article.setType(ArticleType.FREE);
		article.setUpdatedTime(new Date());
		
		Integer userId = 8;
		User user = entityManager.find(User.class, userId);
		
		article.setUser(user);
		
		Article savedArticle = repo.save(article);
		
		assertTrue(savedArticle.getId() > 0);
	}	
	
	@Test
	public void testListAll() {
		List<Article> listArticles = repo.findAll();
		assertFalse(listArticles.isEmpty());
		
	}
}
