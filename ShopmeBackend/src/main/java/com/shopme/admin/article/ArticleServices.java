package com.shopme.admin.article;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Article;
import com.shopme.common.entity.User;

@Service
@Transactional
public class ArticleServices {
	public static final int ARTICLES_PER_PAGE = 5;
	
	@Autowired
	private ArticleRepository repo;
	
	public List<Article> listAll() {
		return repo.findAll();
	}
	
	public Page<Article> listAll(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, ARTICLES_PER_PAGE, sort);
		
		if (keyword != null) {
			return repo.findAll(keyword, pageable);
		}
		
		return repo.findAll(pageable);
	}	
	
	public void save(Article article, User user) {
		if (article.getAlias() == null || article.getAlias().isEmpty()) {
			article.setAlias(article.getTitle().replaceAll(" ", "-"));
		}
				
		article.setUpdatedTime(new Date());
		article.setUser(user);
		
		repo.save(article);
	}
	
	public Article get(Integer id) throws ArticleNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new ArticleNotFoundException("Could not find any article with ID " + id);
		}
	}
	
	public void delete(Integer id) {
		repo.deleteById(id);
	}
	
	public void publish(Integer id) {
		repo.updateEnabledStatus(id, true);
	}
	
	public void unpublish(Integer id) {
		repo.updateEnabledStatus(id, false);
	}	
}
