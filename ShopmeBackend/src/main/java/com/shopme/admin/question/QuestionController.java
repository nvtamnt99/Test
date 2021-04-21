package com.shopme.admin.question;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.Question;

@Controller
public class QuestionController {

	@Autowired
	private QuestionServices service;
	
	@GetMapping("/questions")
	public String listAll(Model model) {
		return listByPage(model, 1, "askTime", "desc", null);
	}
	
	@GetMapping("/questions/page/{pageNum}")
	public String listByPage(Model model, 
						@PathVariable(name = "pageNum") int pageNum,
						@Param("sortField") String sortField,
						@Param("sortDir") String sortDir,
						@Param("keyword") String keyword
			) {
		
		Page<Question> page = service.listAll(pageNum, sortField, sortDir, keyword);
		List<Question> listQuestions = page.getContent();
		
		model.addAttribute("listQuestions", listQuestions);
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		long startCount = (pageNum - 1) * QuestionServices.QUESTIONS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + QuestionServices.QUESTIONS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		if (page.getTotalPages() > 1) {
			model.addAttribute("pageTitle", "Questions (page " + pageNum + ")");
		} else {
			model.addAttribute("pageTitle", "Questions");
		}
		
		return "question/questions";		
	}
	
	@GetMapping("/questions/detail/{id}")
	public String viewQuestion(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			Question question = service.get(id);
			model.addAttribute("question", question);
			
			return "question/question_detail_modal";
		} catch (QuestionNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return "redirect:/questions";			
		}
	}
	
	@GetMapping("/questions/edit/{id}")
	public String editQuestion(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			Question question = service.get(id);
			model.addAttribute("question", question);
			model.addAttribute("pageTitle", "Edit Question (ID: " + id + ")");
			
			return "question/question_form";
		} catch (QuestionNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return "redirect:/questions";			
		}
	}
	
	@PostMapping("/questions/save")
	public String saveQuestion(Question question, RedirectAttributes ra,
			@AuthenticationPrincipal ShopmeUserDetails userDetails) {
		try {
			service.save(question, userDetails.getUser());
			ra.addFlashAttribute("message", "The Question ID " + question.getId() + " has been updated successfully.");
		} catch (QuestionNotFoundException ex) {
			ra.addFlashAttribute("message", "Could not find any question with ID " + question.getId());
		}
		return "redirect:/questions";
	}
	
	@GetMapping("/questions/{id}/approve")
	public String approveQuestion(@PathVariable("id") Integer id, RedirectAttributes ra) {
		service.approve(id);
		ra.addFlashAttribute("message", "The Question ID " + id + " has been approved.");
		return "redirect:/questions";
	}
	
	@GetMapping("/questions/{id}/disapprove")
	public String disapproveQuestion(@PathVariable("id") Integer id, RedirectAttributes ra) {
		service.disapprove(id);
		ra.addFlashAttribute("message", "The Question ID " + id + " has been disapproved.");
		return "redirect:/questions";
	}
	
	@GetMapping("/questions/delete/{id}")
	public RedirectView deleteQuestion(@PathVariable(name = "id") Integer id, RedirectAttributes ra) throws IOException {
		RedirectView rv = new RedirectView("/questions", true);
		
		try {
			service.delete(id);
			
			ra.addFlashAttribute("message", String.format("The question ID %d has been deleted successfully.", id));
		} catch (QuestionNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
					
		return rv;
	}
	
	@GetMapping("/questions/export")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		response.setContentType("text/csv");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=questions_" + currentDateTime + ".csv";
		response.setHeader(headerKey, headerValue);		
		
		List<Question> listQuestions = service.listAll();
		
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		String[] csvHeader = {"Question ID", "Product", "Question", "Asker", "Ask Time", "Approved", "Answer", "Answerer", "Answer Time"};
		String[] nameMapping = {"id", "productName", "questionContent", "askerFullName", "askTime", "approved", "answer", "answererFullName", "answerTime"};
		
		csvWriter.writeHeader(csvHeader);
		
		for (Question question : listQuestions) {
			csvWriter.write(question, nameMapping);
		}
		
		csvWriter.close();		
		
	}
}
