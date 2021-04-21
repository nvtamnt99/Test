package com.shopme.site;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.Review;
import com.shopme.site.review.ReviewRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ReviewRepositoryTests {

	@Autowired
	private ReviewRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateNewReview() {
		Integer productId = 55;
		Product product = entityManager.find(Product.class, productId);
		
		Integer customerId = 7;
		Customer customer = entityManager.find(Customer.class, customerId);
		
		Review newReview = new Review();
		newReview.setProduct(product);
		newReview.setCustomer(customer);
		newReview.setHeadline("Perfect customer service and product quality");
		newReview.setComment("Received the product one day before expected. Kind support also.");
		newReview.setRating(5);
		newReview.setVotes(3);
		newReview.setReviewTime(new Date());
		
		Review savedReview = repo.save(newReview);
		
		assertTrue(savedReview.getId() > 0);
	}
	
	@Test
	public void testCountByCustomerAndProductNotFound() {
		Integer customerId = 11;
		Integer productId = 22;
		Long count = repo.countByCustomerAndProduct(customerId, productId);
		
		assertEquals(0, count);
	}
	
	@Test
	public void testCountByCustomerAndProductFound() {
		Integer customerId = 1;
		Integer productId = 48;
		Long count = repo.countByCustomerAndProduct(customerId, productId);
		
		assertEquals(1, count);
	}
	
	@Test
	public void testFindByProduct() {
		Product product = new Product();
		product.setId(48);
		
		Pageable pageable = PageRequest.of(1, 5);
		Page<Review> page = repo.findByProduct(product, pageable);
		
		long totalElements = page.getTotalElements();
		
		assertEquals(1, totalElements);
	}
}
