package com.shopme.site;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.ReviewVote;
import com.shopme.site.review.vote.ReviewVoteRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class ReviewVoteRepositoryTests {

	@Autowired private ReviewVoteRepository repo;
	
	@Autowired private TestEntityManager entityManager;
	
	@Test
	public void testVoteOneReview() {
		Review review = entityManager.find(Review.class, 1);
		Customer customer = entityManager.find(Customer.class, 1);
		
		ReviewVote newVote = new ReviewVote();
		newVote.setCustomer(customer);
		newVote.setReview(review);
		newVote.downVote();
		
		ReviewVote savedVote = repo.save(newVote);
		
		assertTrue(savedVote.getId() > 0);
	}
}
