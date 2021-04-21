package com.shopme.site.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Product;
import com.shopme.common.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

	@Query("SELECT count(r.id) FROM Review r WHERE r.customer.id = ?1 AND r.product.id = ?2")
	public Long countByCustomerAndProduct(Integer customerId, Integer productId);

	public Page<Review> findByProduct(Product product, Pageable pageable);
	
	@Query("UPDATE Review r SET r.votes = r.votes + ?2 WHERE r.id = ?1")
	@Modifying
	public void vote(Integer reviewId, int score);

	@Query("SELECT r FROM Review r WHERE r.customer.id = ?1")
	public Page<Review> findByCustomer(Integer customerId, Pageable pageable);
	
	@Query("SELECT r FROM Review r WHERE r.customer.id = ?1 AND ("
			+ "r.headline LIKE %?2% OR r.comment LIKE %?2% OR "
			+ "r.product.name LIKE %?2%)")
	public Page<Review> findByCustomer(Integer customerId, String keyword, Pageable pageable);
	
	@Query("SELECT r FROM Review r WHERE r.customer.id = ?1 AND r.id = ?2")
	public Review findByCustomerAndId(Integer customerId, Integer reviewId);	
}
