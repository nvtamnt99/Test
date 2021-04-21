package com.shopme.admin.section;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.common.entity.Product;
import com.shopme.common.entity.ProductSection;
import com.shopme.common.entity.Section;
import com.shopme.common.entity.SectionType;

@Controller
public class ProductSectionController {

	@Autowired
	private SectionServices sectionService;
	
	@GetMapping("/sections/new/product")
	public String showNewProductSectionForm(Model model) {
		Section section = new Section(true, SectionType.PRODUCT);
		
		model.addAttribute("section", section);
		model.addAttribute("pageTitle", "Add Product Section");
		
		return "sections/product_section_form";
	}
	
	
	@PostMapping("/sections/save/product")
	public String saveProductSection(Section section, HttpServletRequest request) {
		addProductsToSection(section, request);
		sectionService.saveSection(section);
		
		return "redirect:/sections";
	}
	
	private void addProductsToSection(Section section, HttpServletRequest request) {
		String[] productIds = request.getParameterValues("productId");
		String[] productSectionIds = request.getParameterValues("productSectionId");
		
		if (productIds != null && productIds.length > 0) {
			for (int i = 0; i < productIds.length; i++) {
				ProductSection productSection = new ProductSection();
				
				if (productSectionIds != null && productSectionIds.length > 0) {
					if (i < productSectionIds.length) {
						Integer productSectionId = Integer.valueOf(productSectionIds[i]);
						productSection.setId(productSectionId);
					}
				}
					
				productSection.setProductOrder(i);
				Integer productId = Integer.valueOf(productIds[i]);
				productSection.setProduct(new Product(productId));
				
				section.addProductSection(productSection);
			}
		}
	}
	
	@GetMapping("/sections/edit/Product/{id}")
	public String editProductSection(@PathVariable(name = "id") Integer id, RedirectAttributes ra,
			Model model) {
		try {
			Section section = sectionService.getSection(id);
			
			model.addAttribute("section", section);
			model.addAttribute("pageTitle", "Edit Product Section (ID: " + id + ")");
			
			return "sections/product_section_form";
			
		} catch (SectionNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return "redirect:/sections";		
		}
		
	}
	
}
