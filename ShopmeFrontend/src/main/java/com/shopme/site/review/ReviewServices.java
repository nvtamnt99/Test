package com.shopme.site.review;

import java.util.Date;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.OrderStatus;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.Review;
import com.shopme.site.order.OrderDetailRepository;
import com.shopme.site.product.ProductRepository;

@Service
@Transactional
public class ReviewServices {

	public static final int REVIEWS_PER_PAGE = 2;
	
	@Autowired
	private ReviewRepository reviewRepo;
	
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private OrderDetailRepository orderDetailRepo;
	
	public boolean didCustomerReviewProduct(Customer customer, Integer productId) {
		Long count = reviewRepo.countByCustomerAndProduct(customer.getId(), productId);
		return count > 0;		
	}
	
	public boolean canCustomerReviewProduct(Customer customer, Integer productId) {
		Long count = orderDetailRepo.countByProductAndCustomerAndOrderStatus(
								productId, customer.getId(), OrderStatus.DELIVERED);
		return count > 0;
	}
	
	public Review save(Review review) {
		review.setReviewTime(new Date());
		Review savedReview = reviewRepo.save(review);
		
		Integer productId = review.getProduct().getId();
		productRepo.increaseReviewCount(productId);
		productRepo.updateAverageRating(productId);
		
		return savedReview;
	}
	
	public Page<Review> listByProduct(Product product, int pageNum, 
			String sortField, String sortDir) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending(); 
		Pageable pageable = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);
		
		return reviewRepo.findByProduct(product, pageable);
	}
	
	public Page<Review> listTop3VotedReviewsByProduct(Integer productId) {
		Sort sort = Sort.by("votes").descending();
		Pageable pageable = PageRequest.of(0, 3, sort);
		
		return reviewRepo.findByProduct(new Product(productId), pageable);		
	}
	
	public int getVotesForReview(Integer reviewId) {
		Review review = reviewRepo.findById(reviewId).get();
		return review.getVotes();
	}
	
	public Page<Review> listReviewsByCustomer(Customer customer, String keyword, int pageNum, 
			String sortField, String sortDir) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);
		
		if (keyword != null) {
			return reviewRepo.findByCustomer(customer.getId(), keyword, pageable);
		}
		
		return reviewRepo.findByCustomer(customer.getId(), pageable);
	}	
	
	public Review getByCustomerAndId(Customer customer, Integer questionId) {
		return reviewRepo.findByCustomerAndId(customer.getId(), questionId);
	}	
}
