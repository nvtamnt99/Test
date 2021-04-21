package com.shopme.site.question;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.Question;
import com.shopme.site.customer.CustomerServices;
import com.shopme.site.product.ProductNotFoundException;
import com.shopme.site.product.ProductServices;
import com.shopme.site.question.vote.QuestionVoteServices;

@Controller
public class QuestionController {

	@Autowired
	private QuestionServices questionService;
	
	@Autowired
	private ProductServices productService;
	
	@Autowired CustomerServices customerService;
	
	@Autowired QuestionVoteServices voteService;	
	
	@GetMapping("/ask_question/{productAlias}")
	public String askQuestion(@PathVariable(name = "productAlias") String productAlias) {
		return "redirect:/p/" + productAlias + "#qa";
	}
	
	@GetMapping("/questions/{productAlias}") 
	public String listQuestionsOfProduct(@PathVariable(name = "productAlias") String productAlias,
			Model model, @AuthenticationPrincipal Authentication authentication) throws ProductNotFoundException {
		return listQuestionsOfProductByPage(model, authentication, productAlias, 1, "votes", "desc");
	}
	
	@GetMapping("/questions/{productAlias}/page/{pageNum}") 
	public String listQuestionsOfProductByPage(
				Model model, @AuthenticationPrincipal Authentication authentication,
				@PathVariable(name = "productAlias") String productAlias,
				@PathVariable(name = "pageNum") int pageNum,
				@Param("sortField") String sortField,
				@Param("sortDir") String sortDir			
			) throws ProductNotFoundException {
		Page<Question> page = questionService.listQuestionsOfProduct(productAlias, pageNum, sortField, sortDir);
		List<Question> listQuestions = page.getContent();
		Product product = productService.getProduct(productAlias);
		
		Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
		if (customer != null) {
			voteService.markQuestionsVotedForProductByCustomer(listQuestions, product.getId(), customer.getId());
		}				
				
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		model.addAttribute("listQuestions", listQuestions);
		model.addAttribute("product", product);

		long startCount = (pageNum - 1) * QuestionServices.QUESTIONS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + QuestionServices.QUESTIONS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		if (page.getTotalPages() > 1) {
			model.addAttribute("pageTitle", "Page " + pageNum + " | Questions for product: " + product.getName());
		} else {
			model.addAttribute("pageTitle", "Questions for product: " + product.getName());
		}		
		
		return "product/product_questions";
	}	

	@GetMapping("/customer/questions") 
	public String listQuestionsByCustomer(Model model, @AuthenticationPrincipal Authentication authentication) {
		return listQuestionsByCustomerByPage(model, authentication, 1, null, "askTime", "desc");
	}
	
	@GetMapping("/customer/questions/page/{pageNum}") 
	public String listQuestionsByCustomerByPage(
				Model model, @AuthenticationPrincipal Authentication authentication,
				@PathVariable(name = "pageNum") int pageNum,
				@Param("keyword") String keyword,
				@Param("sortField") String sortField,
				@Param("sortDir") String sortDir			
			) {
		Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
		Page<Question> page = questionService.listQuestionsByCustomer(customer, keyword, pageNum, sortField, sortDir);		
		List<Question> listQuestions = page.getContent();
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		model.addAttribute("listQuestions", listQuestions);

		long startCount = (pageNum - 1) * QuestionServices.QUESTIONS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + QuestionServices.QUESTIONS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		if (page.getTotalPages() > 1) {
			model.addAttribute("pageTitle", "Page " + pageNum + " | My Questions");
		} else {
			model.addAttribute("pageTitle", "My Questions ");
		}		
		
		return "question/customer_questions";
	}
	
	@GetMapping("/customer/questions/detail/{id}")
	public String viewQuestion(@PathVariable("id") Integer id, Model model, RedirectAttributes ra, 
			@AuthenticationPrincipal Authentication authentication) {
		Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
		Question question = questionService.getByCustomerAndId(customer, id);
		
		if (question != null) {	
			model.addAttribute("question", question);
			return "question/question_detail_modal";
		} else {
			ra.addFlashAttribute("message", "Could not find any question with ID " + id);
			return "redirect:/customer/questions";			
		}
	}	
}
