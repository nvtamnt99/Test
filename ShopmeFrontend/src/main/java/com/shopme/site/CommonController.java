package com.shopme.site;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.common.entity.Category;
import com.shopme.common.entity.Section;
import com.shopme.common.entity.SectionType;
import com.shopme.site.category.CategoryServices;
import com.shopme.site.section.SectionRepository;

@Controller
public class CommonController {

	@Autowired
	private CategoryServices categoryService;
	
	@Autowired
	private SectionRepository sectionRepo;
	
	@GetMapping("")
	public String viewHomepage(Model model) {
		List<Section> listSections = sectionRepo.findAllByEnabledOrderBySectionOrderAsc(true);
		model.addAttribute("listSections", listSections);
		
		if (hasAllCategoriesSection(listSections)) {
			List<Category> categories = categoryService.listNoChildrenCategories();
			model.addAttribute("categories", categories);
		}
		
		model.addAttribute("pageTitle", "Home");
		return "index";
	}
	
	private boolean hasAllCategoriesSection(List<Section> listSections) {
		for (Section section : listSections) {
			if (section.getType().equals(SectionType.ALL_CATEGORIES)) {
				return true;
			}
		}
		
		return false;
	}

	@GetMapping("/order_complete")
	public String testOrderCompletePage(Model model) {
		
		model.addAttribute("pageTitle", "Order Completed");
		
		return "order_completed";
	}
	
	@GetMapping("/login")
	public String showLoginForm(Model model) {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			model.addAttribute("pageTitle", "Customer Login");
			return "login";
		}

		return "redirect:/";
	}

	@GetMapping("/show_delete_modal")
	public String showDeleteModal(@Param("type") String type, @Param("id") String id,
			@Param("keyField") String keyField, Model model) {
		String deleteInfo = String.format("%s ID %s: %s?", type, id, keyField);
		String deleteURL = String.format("/%s/delete/%s", type, id);
		
		model.addAttribute("deleteTarget", deleteInfo);
		model.addAttribute("deleteURL", deleteURL);
		
		return "delete_modal";
	}	
}
