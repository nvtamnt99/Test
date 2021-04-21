package com.shopme.admin.article;

import java.util.List;

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

import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.Article;

@Controller
public class ArticleController {

	@Autowired
	private ArticleServices service;
	
	@GetMapping("/articles")
	public String listAll(Model model) {
		return listByPage(model, 1, "title", "asc", null);
	}
	
	@GetMapping("/articles/page/{pageNum}")
	public String listByPage(Model model, 
						@PathVariable(name = "pageNum") int pageNum,
						@Param("sortField") String sortField,
						@Param("sortDir") String sortDir,
						@Param("keyword") String keyword
			) {
		
		Page<Article> page = service.listAll(pageNum, sortField, sortDir, keyword);
		List<Article> listArticles = page.getContent();
		
		model.addAttribute("listArticles", listArticles);
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		long startCount = (pageNum - 1) * ArticleServices.ARTICLES_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + ArticleServices.ARTICLES_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		if (page.getTotalPages() > 1) {
			model.addAttribute("pageTitle", "Articles (page " + pageNum + ")");
		} else {
			model.addAttribute("pageTitle", "Articles");
		}
		
		return "articles/articles";
	}	
	
	@GetMapping("/articles/new")
	public String newArticle(Model model) {
		model.addAttribute("article", new Article());
		model.addAttribute("pageTitle", "Create New Article");
		
		return "articles/article_form";
	}
	
	@PostMapping("/articles/save")
	public String saveArticle(Article article, RedirectAttributes ra, 
			@AuthenticationPrincipal ShopmeUserDetails userDetails) {
		
		service.save(article, userDetails.getUser());
		
		ra.addFlashAttribute("message", "The article has been saved successfully.");
		
		return "redirect:/articles";
	}
	
	@GetMapping("/articles/edit/{id}")
	public String editArticle(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes ra) {
		try {
			Article article = service.get(id);
			model.addAttribute("article", article);
			model.addAttribute("pageTitle", "Edit Article (ID: " + id + ")");
			
			return "articles/article_form"; 
			
		} catch (ArticleNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			
			return "articles/articles";
		}
		
	}
	
	@GetMapping("/articles/delete/{id}")
	public String deleteArticle(@PathVariable(name = "id") Integer id, RedirectAttributes ra) {
		try {
			service.get(id);
			service.delete(id);
			
			ra.addFlashAttribute("message", "The article ID " + id + " has been deleted.");
		} catch (ArticleNotFoundException ex) {
			ra.addFlashAttribute("message", "Could not find any article with ID " + id);
		}
		
		return "redirect:/articles";
	}
	
	@GetMapping("/articles/publish/{id}")
	public String publishArticle(@PathVariable(name = "id") Integer id, RedirectAttributes ra) {
		try {
			service.get(id);
			service.publish(id);
			
			ra.addFlashAttribute("message", "The article ID " + id + " has been published.");
		} catch (ArticleNotFoundException ex) {
			ra.addFlashAttribute("message", "Could not find any article with ID " + id);
		}
		
		return "redirect:/articles";
	}
	
	@GetMapping("/articles/unpublish/{id}")
	public String unpublishArticle(@PathVariable(name = "id") Integer id, RedirectAttributes ra) {
		try {
			service.get(id);
			service.unpublish(id);
			
			ra.addFlashAttribute("message", "The article ID " + id + " has been unpublished.");
		} catch (ArticleNotFoundException ex) {
			ra.addFlashAttribute("message", "Could not find any article with ID " + id);
		}
		
		return "redirect:/articles";
	}	
	
	@GetMapping("/articles/detail/{id}")
	public String viewDetail(@PathVariable(name = "id") Integer id, RedirectAttributes ra,  Model model) {
		try {
			Article article = service.get(id);
			model.addAttribute("article", article);
			ra.addFlashAttribute("message", "The article ID " + id + " has been unpublished.");
			
			return "articles/article_detail_modal";
			
		} catch (ArticleNotFoundException ex) {
			ra.addFlashAttribute("message", "Could not find any article with ID " + id);
			return "redirect:/articles";
		}
		
	}
}
