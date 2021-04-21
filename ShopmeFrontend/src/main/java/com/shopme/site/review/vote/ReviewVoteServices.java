package com.shopme.site.review.vote;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.ReviewVote;
import com.shopme.site.review.ReviewRepository;

@Service
@Transactional
public class ReviewVoteServices {

	@Autowired private ReviewVoteRepository voteRepo;
	
	@Autowired private ReviewRepository reviewRepo;
	
	public void voteUp(Integer reviewId, Customer customer) throws ReviewVoteException {
		System.out.println("ReviewVoteServices > voteUp");
		ReviewVote vote = voteRepo.findByReviewAndCustomer(reviewId, customer.getId());
		int score = 0;
		
		if (vote != null) {
			if (vote.isUpvoted()) {
				score = -1;
				undoVote(vote, reviewId, score);
				throw new ReviewVoteException("You have unvoted up that review.");
			} else {
				score = 2;
			}
		} else {
			score = 1;
			Review review = reviewRepo.findById(reviewId).get();
			vote = new ReviewVote();
			vote.setReview(review);
			vote.setCustomer(customer);
		}
		
		vote.upVote();
		voteRepo.save(vote);
		reviewRepo.vote(reviewId, score);
	}

	public void voteDown(Integer reviewId, Customer customer) throws ReviewVoteException {
		System.out.println("ReviewVoteServices > voteDown");
		ReviewVote vote = voteRepo.findByReviewAndCustomer(reviewId, customer.getId());
		int score = 0;
		
		if (vote != null) {
			if (vote.isDownvoted()) {
				score = +1;
				undoVote(vote, reviewId, score);
				throw new ReviewVoteException("You have unvoted down that review.");
			} else {
				score = -2;
			}
		} else {
			score = -1;
			Review review = reviewRepo.findById(reviewId).get();
			vote = new ReviewVote();
			vote.setReview(review);
			vote.setCustomer(customer);
		}
		
		vote.downVote();;
		voteRepo.save(vote);
		reviewRepo.vote(reviewId, score);
	}
	
	private void undoVote(ReviewVote vote, Integer reviewId, int score) {
		voteRepo.delete(vote);
		reviewRepo.vote(reviewId, score);
	}
	
	public void markReviewsVotedForProductByCustomer(List<Review> listReviews, Integer productId, Integer customerId) {
		List<ReviewVote> reviewVotes = voteRepo.findByProductAndCustomer(productId, customerId);
		
		for (ReviewVote aVote : reviewVotes) {
			Review votedReview = aVote.getReview();
			if (listReviews.contains(votedReview)) {
				int index = listReviews.indexOf(votedReview);
				Review review = listReviews.get(index);
				
				if (aVote.isUpvoted()) {
					review.setUpvotedByCurrentCustomer(true);
				} else if (aVote.isDownvoted()) {
					review.setDownvotedByCurrentCustomer(true);
				}
			}
		}
	}
}
