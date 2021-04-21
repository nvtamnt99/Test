package com.shopme.admin.review;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.admin.order.OrderNotFoundException;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;

@Controller
public class ReviewController {

	@Autowired
	private ReviewServices service;
	
	@GetMapping("/reviews")
	public String listAll(Model model, HttpServletRequest request) {
		return listByPage(model, request, 1, "reviewTime", "desc", null);
	}
	
	@GetMapping("/reviews/page/{pageNum}")
	public String listByPage(Model model, HttpServletRequest request,
						@PathVariable(name = "pageNum") int pageNum,
						@Param("sortField") String sortField,
						@Param("sortDir") String sortDir,
						@Param("keyword") String keyword
			) {
		
		Page<Review> page = service.listAll(pageNum, sortField, sortDir, keyword);
		List<Review> listReviews = page.getContent();
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("listReviews", listReviews);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		long startCount = (pageNum - 1) * ReviewServices.REVIEWS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + ReviewServices.REVIEWS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		if (page.getTotalPages() > 1) {
			model.addAttribute("pageTitle", "Reviews (page " + pageNum + ")");
		} else {
			model.addAttribute("pageTitle", "Reviews");
		}
		
		return "reviews/reviews";
	}	
	
	@GetMapping("/reviews/detail/{id}")
	public String viewReview(@PathVariable("id") Integer id, Model model, RedirectAttributes ra,
			HttpServletRequest request) {
		try {
			Review review = service.get(id);
			model.addAttribute("review", review);
			
			return "reviews/review_detail_modal";
		} catch (ReviewNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return "redirect:/reviews";
		}
		
	}
	
	@GetMapping("/reviews/delete/{id}")
	public String deleteReview(@PathVariable("id") Integer id, Model model, RedirectAttributes ra,
			HttpServletRequest request) {
		try {
			service.delete(id);
			
			ra.addFlashAttribute("message", "The review ID " + id + " has been deleted.");
			
		} catch (ReviewNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/reviews";
		
	}
	
	@GetMapping("/reviews/edit/{id}")
	public String editReview(@PathVariable("id") Integer id, Model model, RedirectAttributes ra,
			HttpServletRequest request) {
		try {
			Review review = service.get(id);
			
			model.addAttribute("review", review);
			model.addAttribute("pageTitle", "Edit Review (ID: " + id + ")");
			
			return "reviews/review_form";
			
		} catch (ReviewNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return "redirect:/reviews";
		}		
	}	
	
	@PostMapping("/reviews/save")
	public String saveReview(Review review, RedirectAttributes ra) {
		service.save(review);
		ra.addFlashAttribute("message", "The review ID "
				+ review.getId() + " has been updated successfully.");
		
		return "redirect:/reviews";
	}
	
	@GetMapping("/reviews/export")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		response.setContentType("text/csv");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=reviews_" + currentDateTime + ".csv";
		response.setHeader(headerKey, headerValue);
		
		List<Review> reviews = service.listAll();
		
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		String[] csvHeader = {"Review ID", "Product", "Customer", "Rating", "Headline", "Comment", "Votes", "Time"};
		String[] nameMapping = {"id", "productName", "customerName", "rating", "headline", "comment", "votes", "reviewTime"};
		
		csvWriter.writeHeader(csvHeader);		

		for (Review review : reviews) {
			csvWriter.write(review, nameMapping);
		}
		
		csvWriter.close();
	}	
}
