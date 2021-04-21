package com.shopme.admin.section;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.common.entity.Section;

@Controller
public class GeneralSectionController {

	@Autowired
	private SectionServices sectionService;

	@GetMapping("/sections")
	public String listAllSections(Model model) {
		List<Section> listSections = sectionService.listSections();
		model.addAttribute("pageTitle", "Homepage Settings");
		model.addAttribute("listSections", listSections);
		
		return "sections/sections";
	}
	
	
	@GetMapping("/sections/enable/{id}")
	public String enableSection(@PathVariable(name = "id") Integer id, RedirectAttributes ra) {
		try {
			sectionService.enableSection(id);			
			ra.addFlashAttribute("message", "The section ID " + id + " has been enabled.");
		} catch (SectionNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		
		return "redirect:/sections";
	}
	
	@GetMapping("/sections/disable/{id}")
	public String disableSection(@PathVariable(name = "id") Integer id, RedirectAttributes ra) {
		try {
			sectionService.disableSection(id);			
			ra.addFlashAttribute("message", "The section ID " + id + " has been disabled.");
		} catch (SectionNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		
		return "redirect:/sections";
	}
	
	@GetMapping("/sections/moveup/{id}")
	public String moveSectionUp(@PathVariable(name = "id") Integer id, RedirectAttributes ra) {
		try {
			sectionService.moveSectionUp(id);
			
			ra.addFlashAttribute("message", "The section ID " + id + " has been moved up by one position.");
			
		} catch (SectionUnmoveableException | SectionNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		
		return "redirect:/sections";		
	}
	
	@GetMapping("/sections/movedown/{id}")
	public String moveSectionDown(@PathVariable(name = "id") Integer id, RedirectAttributes ra) {
		try {
			sectionService.moveSectionDown(id);
			ra.addFlashAttribute("message", "The section ID " + id + " has been moved down by one position.");
			
		} catch (SectionUnmoveableException | SectionNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		
		return "redirect:/sections";		
	}
	
	
	@GetMapping("/sections/delete/{id}")
	public String deleteSection(@PathVariable(name = "id") Integer id, RedirectAttributes ra) {
		try {
			sectionService.deleteSection(id);
			ra.addFlashAttribute("message", "The section ID " + id + " has been deleted.");
			
		} catch (SectionNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		
		return "redirect:/sections";		
	}

}
