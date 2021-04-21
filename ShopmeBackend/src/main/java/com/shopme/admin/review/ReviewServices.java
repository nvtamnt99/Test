package com.shopme.admin.review;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Review;

@Service
public class ReviewServices {
	public static final int REVIEWS_PER_PAGE = 5;
	
	@Autowired
	private ReviewRepository repo;
	
	public List<Review> listAll() {
		return repo.findAll();
	}
	
	public Page<Review> listAll(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);
		
		if (keyword != null) {
			return repo.findAll(keyword, pageable);
		}
		
		return repo.findAll(pageable);		
	}
	
	public Review get(Integer id) throws ReviewNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new ReviewNotFoundException("Could not find any review with ID " + id);
		}
	}
	
	public void delete(Integer id) throws ReviewNotFoundException {
		Long count = repo.countById(id);
		if (count == null || count == 0) {
			throw new ReviewNotFoundException("Could not find any review with ID " + id); 
		}
		
		repo.deleteById(id);
	}
	
	public void save(Review review) {
		repo.save(review);
	}
}
