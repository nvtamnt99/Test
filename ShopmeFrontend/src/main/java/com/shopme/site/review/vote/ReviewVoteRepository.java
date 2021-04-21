package com.shopme.site.review.vote;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.ReviewVote;

@Repository
public interface ReviewVoteRepository extends JpaRepository<ReviewVote, Integer> {
	
	@Query("SELECT rv FROM ReviewVote rv WHERE rv.review.id = ?1 AND rv.customer.id = ?2")
	public ReviewVote findByReviewAndCustomer(Integer reviewId, Integer customerId);
	
	@Query("SELECT rv FROM ReviewVote rv WHERE rv.review.product.id = ?1 AND rv.customer.id = ?2")
	public List<ReviewVote> findByProductAndCustomer(Integer productId, Integer customerId);
}
