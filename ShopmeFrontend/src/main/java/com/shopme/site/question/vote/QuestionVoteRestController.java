package com.shopme.site.question.vote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.Customer;
import com.shopme.site.customer.CustomerServices;

@RestController
public class QuestionVoteRestController {

	@Autowired
	private QuestionVoteServices service;
	
	@Autowired
	private CustomerServices customerService;
	
	@PostMapping("/vote_question/{id}/{type}")
	public String voteQuestion(@PathVariable(name = "id") Integer questionId,
			@PathVariable(name = "type") String voteType,
			@AuthenticationPrincipal Authentication auth) {
		
		if (auth == null || auth instanceof AnonymousAuthenticationToken) {
			return "You must login to vote questions";
		}
		
		Customer customer = customerService.getCurrentlyLoggedInCustomer(auth);
		if (customer == null) {
			return "You don't have permission to vote questions";
		}
		
		try {
			if (voteType.equals("up")) {
				service.voteUp(questionId, customer);
			} else if (voteType.equals("down")) {
				service.voteDown(questionId, customer);
			}
		} catch (QuestionVoteException ex) {
			return ex.getMessage();
		}
		
		return "You have successfully voted " + voteType + " that question.";
	}
	
}
