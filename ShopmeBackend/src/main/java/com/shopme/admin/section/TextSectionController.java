package com.shopme.admin.section;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.common.entity.Section;
import com.shopme.common.entity.SectionType;

@Controller
public class TextSectionController {

	@Autowired
	private SectionServices sectionService;
	
	@GetMapping("/sections/new/text")
	public String showNewTextSectionForm(Model model) {
		Section section = new Section(true, SectionType.TEXT);
		
		model.addAttribute("section", section);
		model.addAttribute("pageTitle", "Add Text Section");
		
		return "sections/text_section_form";
	}	
	
	@PostMapping("/sections/save/text")
	public String saveTextSection(Section section, HttpServletRequest request) {
		sectionService.saveSection(section);
		return "redirect:/sections";
	}		
	
	@GetMapping("/sections/edit/Text/{id}")
	public String editTextSection(@PathVariable(name = "id") Integer id, RedirectAttributes ra,
			Model model) {
		try {
			Section section = sectionService.getSection(id);
			
			model.addAttribute("section", section);
			model.addAttribute("pageTitle", "Edit Text Section (ID: " + id + ")");
			
			return "sections/text_section_form";
			
		} catch (SectionNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return "redirect:/sections";		
		}
		
	}	
}
