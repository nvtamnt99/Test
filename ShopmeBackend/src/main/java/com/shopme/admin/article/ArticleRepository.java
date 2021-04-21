package com.shopme.admin.article;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Article;
import com.shopme.common.entity.ArticleType;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {
	@Query("UPDATE Article a SET a.published = ?2 WHERE a.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean published);
	
	
	@Query("SELECT a FROM Article a WHERE a.title LIKE %?1% OR a.content LIKE %?1%")
	public Page<Article> findAll(String keyword, Pageable pageable);
	
	public List<Article> findByTypeOrderByTitle(ArticleType type);
	
}
