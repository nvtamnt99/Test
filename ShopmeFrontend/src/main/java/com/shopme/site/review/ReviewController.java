package com.shopme.site.review;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.Review;
import com.shopme.site.customer.CustomerServices;
import com.shopme.site.product.ProductNotFoundException;
import com.shopme.site.product.ProductServices;
import com.shopme.site.review.vote.ReviewVoteServices;

@Controller
public class ReviewController {

	@Autowired private CustomerServices customerService;
	
	@Autowired private ReviewServices reviewService;
	
	@Autowired private ProductServices productService;
	
	@Autowired ReviewVoteServices reviewVoteService;
	
	@GetMapping("/write_review/product/{id}")
	public String showReviewForm(@PathVariable("id") Integer productId, Model model,
			@AuthenticationPrincipal Authentication authentication) {
		
		Review review = new Review();
		
		try {
			Product product = productService.get(productId);
			review.setProduct(product);
			model.addAttribute("product", product);
		} catch (ProductNotFoundException ex) {
			model.addAttribute("title", "Write Review");
			model.addAttribute("message", "No product found with the given ID " + productId);
			model.addAttribute("pageTitle", "Error Write Review");	
			
			return "message";
		}
		
		Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
		if (customer != null) {
			
			boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, productId);
			
			if (customerReviewed) {
				model.addAttribute("customerReviewed", true);
			} else {
				boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, productId);
				if (customerCanReview) {
					model.addAttribute("customerCanReview", true);
				} else {
					model.addAttribute("NoReviewPermission", true);
				}
			}
		}
		
		
		model.addAttribute("review", review);
		model.addAttribute("pageTitle", "Write Product Review");
		return "review/review_form";
	}
	
	@PostMapping("/post_review")
	public String saveReview(Model model, Review review, 
			@AuthenticationPrincipal Authentication authentication) {
		Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
		review.setCustomer(customer);
		Review savedReview = reviewService.save(review);
		model.addAttribute("pageTitle", "Write Product Review");
		model.addAttribute("review", savedReview);
		
		return "review/review_done";
	}
	
	@GetMapping("/reviews/{productAlias}")
	public String listReviewsOfProduct(@PathVariable(name = "productAlias") String productAlias,
			Model model, @AuthenticationPrincipal Authentication authentication) throws ProductNotFoundException {
		return listReviewsOfProductByPage(model, productAlias, 1, "votes", "desc", authentication);
	}
		
	@GetMapping("/reviews/{productAlias}/page/{pageNum}") 
	public String listReviewsOfProductByPage(
				Model model,
				@PathVariable(name = "productAlias") String productAlias,
				@PathVariable(name = "pageNum") int pageNum,
				@Param("sortField") String sortField,
				@Param("sortDir") String sortDir,
				@AuthenticationPrincipal Authentication authentication
			) throws ProductNotFoundException {
		Product product = productService.getProduct(productAlias);
		Page<Review> page = reviewService.listByProduct(product, pageNum, sortField, sortDir);
		List<Review> listReviews = page.getContent();
		
		Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
		if (customer != null) {		
			reviewVoteService.markReviewsVotedForProductByCustomer(listReviews, product.getId(), customer.getId());
		}
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		model.addAttribute("listReviews", listReviews);
		model.addAttribute("product", product);

		long startCount = (pageNum - 1) * ReviewServices.REVIEWS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + ReviewServices.REVIEWS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		if (page.getTotalPages() > 1) {
			model.addAttribute("pageTitle", "Page " + pageNum + " | Reviews for product: " + product.getName());
		} else {
			model.addAttribute("pageTitle", "Reviews for product: " + product.getName());
		}		
		
		return "product/product_reviews";
	}

	@GetMapping("/customer/reviews") 
	public String listReviewsByCustomer(Model model, @AuthenticationPrincipal Authentication authentication) {
		return listReviewsByCustomerByPage(model, authentication, 1, null, "reviewTime", "desc");
	}
				
	@GetMapping("/customer/reviews/page/{pageNum}") 
	public String listReviewsByCustomerByPage(
				Model model, @AuthenticationPrincipal Authentication authentication,
				@PathVariable(name = "pageNum") int pageNum,
				@Param("keyword") String keyword,
				@Param("sortField") String sortField,
				@Param("sortDir") String sortDir			
			) {
		Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
		Page<Review> page = reviewService.listReviewsByCustomer(customer, keyword, pageNum, sortField, sortDir);		
		List<Review> listReviews = page.getContent();
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		model.addAttribute("listReviews", listReviews);

		long startCount = (pageNum - 1) * ReviewServices.REVIEWS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + ReviewServices.REVIEWS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		if (page.getTotalPages() > 1) {
			model.addAttribute("pageTitle", "Page " + pageNum + " | My Reviews");
		} else {
			model.addAttribute("pageTitle", "My Reviews");
		}		
		
		return "review/customer_reviews";
	}
	
	@GetMapping("/customer/reviews/detail/{id}")
	public String viewReview(@PathVariable("id") Integer id, Model model, RedirectAttributes ra, 
			@AuthenticationPrincipal Authentication authentication) {
		Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
		Review review = reviewService.getByCustomerAndId(customer, id);
		
		if (review != null) {	
			model.addAttribute("review", review);
			return "review/review_detail_modal";
		} else {
			ra.addFlashAttribute("message", "Could not find any review with ID " + id);
			return "redirect:/customer/reviews";			
		}
	}	
}
