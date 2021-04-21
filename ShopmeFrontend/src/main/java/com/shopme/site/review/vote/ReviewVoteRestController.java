package com.shopme.site.review.vote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.Customer;
import com.shopme.site.customer.CustomerServices;
import com.shopme.site.review.ReviewServices;

@RestController
public class ReviewVoteRestController {

	@Autowired
	private ReviewVoteServices voteService;
	
	@Autowired
	private CustomerServices customerService;
	
	@Autowired
	private ReviewServices reviewService;
	
	@PostMapping("/vote_review/{id}/{type}")
	public String voteReview(@PathVariable(name = "id") Integer reviewId,
			@PathVariable(name = "type") String voteType,
			@AuthenticationPrincipal Authentication auth) {
		
		if (auth == null || auth instanceof AnonymousAuthenticationToken) {
			return "You must login to vote reviews";
		}
		
		Customer customer = customerService.getCurrentlyLoggedInCustomer(auth);
		if (customer == null) {
			return "You don't have permission to vote review";
		}
		
		try {
			if (voteType.equals("up")) {
				voteService.voteUp(reviewId, customer);
			} else if (voteType.equals("down")) {
				voteService.voteDown(reviewId, customer);
			}
		} catch (ReviewVoteException ex) {
			return ex.getMessage();
		}
		
		return "You have successfully voted " + voteType + " that review.";
	}
	
	@PostMapping("/getreviewvote/{reviewId}")
	public ResponseEntity<?> getVotesForReview(@PathVariable(name = "reviewId") Integer reviewId,
			@AuthenticationPrincipal Authentication auth) {
		if (auth == null || auth instanceof AnonymousAuthenticationToken) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}		
		int votes = reviewService.getVotesForReview(reviewId);
		return new ResponseEntity<String>(String.valueOf(votes), HttpStatus.OK);
	}	
}
