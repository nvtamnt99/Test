package com.shopme.site.question;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Question;
import com.shopme.site.customer.CustomerServices;
import com.shopme.site.product.ProductNotFoundException;

@RestController
public class QuestionRestController {

	@Autowired
	private QuestionServices questionService;
	
	@Autowired
	private CustomerServices customerService;
	
	@PostMapping("/post_question/{productId}")
	public ResponseEntity<?> postQuestion(@RequestBody Question question,
			@PathVariable(name = "productId") Integer productId,
			@AuthenticationPrincipal Authentication authentication) throws ProductNotFoundException, IOException {
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}
		
		Customer customerUser = customerService.getCurrentlyLoggedInCustomer(authentication);
		if (customerUser == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		questionService.saveNewQuestion(question, customerUser, productId);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/getquestionvote/{questionId}")
	public ResponseEntity<?> getVotesForQuestion(@PathVariable(name = "questionId") Integer questionId,
			@AuthenticationPrincipal Authentication auth) {
		if (auth == null || auth instanceof AnonymousAuthenticationToken) {
			return new ResponseEntity<>(HttpStatus.FORBIDDEN);
		}		
		int votes = questionService.getVotesForQuestion(questionId);
		return new ResponseEntity<String>(String.valueOf(votes), HttpStatus.OK);
	}	
}
