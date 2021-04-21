package com.shopme.site.product;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.Question;
import com.shopme.common.entity.Review;
import com.shopme.site.brand.BrandRepository;
import com.shopme.site.category.CategoryServices;
import com.shopme.site.customer.CustomerServices;
import com.shopme.site.question.QuestionServices;
import com.shopme.site.question.vote.QuestionVoteServices;
import com.shopme.site.review.ReviewServices;
import com.shopme.site.review.vote.ReviewVoteServices;

@Controller
public class ProductController {
	@Autowired CategoryServices categoryService;
	
	@Autowired ProductServices productService;
	
	@Autowired QuestionServices questionService;
	
	@Autowired CustomerServices customerService;
	
	@Autowired QuestionVoteServices questionVoteService;
	
	@Autowired ReviewVoteServices reviewVoteService;
	
	@Autowired ReviewServices reviewService;
	
	@Autowired BrandRepository brandRepo;
	
	@GetMapping("/p/{product_alias}")
	public String viewProductDetail(@PathVariable(name = "product_alias") String alias, Model model,
			@AuthenticationPrincipal Authentication authentication) {
		try {
			Product product = productService.getProduct(alias);
			List<Category> parents = categoryService.getCategoryParents(product.getCategory());
			List<Product> recommendedProducts = productService.getRecommendedList(product);
			List<Question> questions = questionService.getTop3VotedQuestions(product.getId());
			
			Page<Review> pageReviews = reviewService.listTop3VotedReviewsByProduct(product.getId());
			List<Review> reviews = pageReviews.getContent();
			
			long numberOfReviews = pageReviews.getTotalElements();
			
			Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
			if (customer != null) {
				questionVoteService.markQuestionsVotedForProductByCustomer(questions, product.getId(), customer.getId());
				reviewVoteService.markReviewsVotedForProductByCustomer(reviews, product.getId(), customer.getId());
				
				boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, product.getId());
				
				if (customerReviewed) {
					model.addAttribute("customerReviewed", true);
				} else {
					boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, product.getId());
					if (customerCanReview) {
						model.addAttribute("customerCanReview", true);
					}
				}
			}
			
			int numberOfQuestions = questionService.getNumberOfQuestions(product.getId());
			int numberOfAnsweredQuestions = questionService.getNumberOfAnsweredQuestions(product.getId());
			
			
			model.addAttribute("parents", parents);	
			model.addAttribute("product", product);
			model.addAttribute("questions", questions);
			
			model.addAttribute("numberOfQuestions", numberOfQuestions);
			model.addAttribute("numberOfAnsweredQuestions", numberOfAnsweredQuestions);
			
			model.addAttribute("reviews", reviews);
			model.addAttribute("numberOfReviews", numberOfReviews);
			
			model.addAttribute("pageTitle", product.getName());
			model.addAttribute("recommendedProducts", recommendedProducts);
			
			return "product/product_detail";
		} catch (ProductNotFoundException ex) {
			return "error/404";
		}
	}

	@GetMapping("/search")
	public String search(@Param("keyword") String keyword, Model model) {
		return searchByPage(keyword, model, 1);
	}
	
	@GetMapping("/search/page/{pageNum}")
	public String searchByPage(@Param("keyword") String keyword, Model model,
			@PathVariable(name = "pageNum") int pageNum) {
		Page<Product> result = productService.search(keyword, pageNum);
		
		List<Product> listResult = result.getContent();
		
		model.addAttribute("totalPages", result.getTotalPages());
		model.addAttribute("totalItems", result.getTotalElements());
		model.addAttribute("currentPage", pageNum);	
		
		long startCount = (pageNum - 1) * ProductServices.SEARCH_RESULT_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + ProductServices.SEARCH_RESULT_PER_PAGE - 1;
		if (endCount > result.getTotalElements()) {
			endCount = result.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);		
		
		model.addAttribute("listResult", listResult);
		model.addAttribute("keyword", keyword);
		model.addAttribute("pageTitle", keyword + " - Search Result");
		
		return "search_result";		
	}
	
	@GetMapping("/c/{category_alias}")
	public String viewCategory(@PathVariable(name = "category_alias") String alias, Model model) {
		return viewCategoryByPage(alias, 1, model);
	}
	
	@GetMapping("/c/{category_alias}/page/{pageNum}")
	public String viewCategoryByPage(@PathVariable(name = "category_alias") String alias,
			@PathVariable(name = "pageNum") int pageNum,
			Model model) {
		Category category = categoryService.getCategory(alias);
		if (category == null) {
			return "error/404";
		}
		
		List<Category> parents = categoryService.getCategoryParents(category);
		
		Page<Product> pageProducts = productService.listByCategory(pageNum, category.getId());
		List<Product> listProducts = pageProducts.getContent();
		
		model.addAttribute("totalPages", pageProducts.getTotalPages());
		model.addAttribute("totalItems", pageProducts.getTotalElements());
		model.addAttribute("currentPage", pageNum);	
		
		long startCount = (pageNum - 1) * ProductServices.PRODUCTS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + ProductServices.PRODUCTS_PER_PAGE - 1;
		if (endCount > pageProducts.getTotalElements()) {
			endCount = pageProducts.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		model.addAttribute("category", category);
		model.addAttribute("catAlias", alias);
		model.addAttribute("parents", parents);
		model.addAttribute("products", listProducts);
		model.addAttribute("pageTitle", category.getName());		
		
		return "products_by_category";
	}	

	@GetMapping("/brand/{brand_id}")
	public String listProductsByBrand(@PathVariable(name = "brand_id") Integer brandId, Model model) {
		return listProductsByBrandByPage(brandId, 1, model);
	}
	
	@GetMapping("/brand/{brand_id}/page/{pageNum}")
	public String listProductsByBrandByPage(@PathVariable(name = "brand_id") Integer brandId,
			@PathVariable(name = "pageNum") int pageNum,
			Model model) {
		Optional<Brand> brandById = brandRepo.findById(brandId);
		if (!brandById.isPresent()) {
			return "error/404";
		}
		
		Brand brand = brandById.get();
		
		Page<Product> pageProducts = productService.listByBrand(pageNum, brand.getId());
		List<Product> listProducts = pageProducts.getContent();
		
		model.addAttribute("totalPages", pageProducts.getTotalPages());
		model.addAttribute("totalItems", pageProducts.getTotalElements());
		model.addAttribute("currentPage", pageNum);	
		
		long startCount = (pageNum - 1) * ProductServices.PRODUCTS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + ProductServices.PRODUCTS_PER_PAGE - 1;
		if (endCount > pageProducts.getTotalElements()) {
			endCount = pageProducts.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		model.addAttribute("brand", brand);
		model.addAttribute("products", listProducts);
		model.addAttribute("pageTitle", "Products by " + brand.getName());		
		
		return "products_by_brand";
	}		
}
