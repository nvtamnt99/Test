package com.shopme.site.article;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {

	@Query("SELECT a FROM Article a WHERE a.alias = ?1")
	public Article findByAlias(String alias);
	
}
