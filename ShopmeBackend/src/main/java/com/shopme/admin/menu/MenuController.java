package com.shopme.admin.menu;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.article.ArticleRepository;
import com.shopme.common.entity.Article;
import com.shopme.common.entity.ArticleType;
import com.shopme.common.entity.Menu;

@Controller
public class MenuController {
	
	@Autowired
	private MenuServices service;
	
	@Autowired
	private ArticleRepository articleRepo;

	@GetMapping("/menus")
	public String listAll(Model model) {
		List<Menu> listMenus = service.listAll();
		model.addAttribute("listMenus", listMenus);
		model.addAttribute("pageTitle", "Menu Items");
		
		return "menus/menus";
	}
	
	@GetMapping("menus/new")
	public String newMenu(Model model) {
		List<Article> listArticles = articleRepo.findByTypeOrderByTitle(ArticleType.MENU_BOUND);
		
		model.addAttribute("menu", new Menu());
		model.addAttribute("listArticles", listArticles);
		model.addAttribute("pageTitle", "Create New Menu Item");
		
		return "menus/menu_form";
	}
	
	@PostMapping("/menus/save")
	public String saveMenu(Menu menu, RedirectAttributes ra) {
		service.save(menu);
		
		ra.addFlashAttribute("message", "The menu item has been saved successfully.");		
		return "redirect:/menus";
	}
	
	@GetMapping("/menus/edit/{id}")
	public String editArticle(@PathVariable(name = "id") Integer id, Model model,
			RedirectAttributes ra) {
		try {
			Menu menu = service.get(id);
			List<Article> listArticles = articleRepo.findByTypeOrderByTitle(ArticleType.MENU_BOUND);
			
			model.addAttribute("menu", menu);
			model.addAttribute("listArticles", listArticles);
			model.addAttribute("pageTitle", "Edit Menu Item (ID: " + id + ")");
			
			return "menus/menu_form";
		} catch (MenuItemNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());		
			return "redirect:/menus";
		}
	}
	
	@GetMapping("/menus/enable/{id}")
	public String enableMenu(@PathVariable(name = "id") Integer id, RedirectAttributes ra) {
		try {
			service.get(id);
			service.enable(id);
			
			ra.addFlashAttribute("message", "The menu item ID " + id + " has been enabled.");
		} catch (MenuItemNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		
		return "redirect:/menus";
	}
	
	@GetMapping("/menus/disable/{id}")
	public String disableMenu(@PathVariable(name = "id") Integer id, RedirectAttributes ra) {
		try {
			service.get(id);
			service.disable(id);
			
			ra.addFlashAttribute("message", "The menu item ID " + id + " has been disabled.");
		} catch (MenuItemNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		
		return "redirect:/menus";
	}
	
	@GetMapping("/menus/delete/{id}")
	public String deleteMenu(@PathVariable(name = "id") Integer id, RedirectAttributes ra) {
		try {
			service.get(id);
			service.delete(id);
			
			ra.addFlashAttribute("message", "The menu item ID " + id + " has been deleted.");
		} catch (MenuItemNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		
		return "redirect:/menus";
	}
	
	@GetMapping("/menus/moveup/{id}")
	public String moveMenuUp(@PathVariable(name = "id") Integer id, RedirectAttributes ra) {
		try {
			service.moveMenuUp(id);
			
			ra.addFlashAttribute("message", "The menu ID " + id + " has been moved up by one position.");
			
		} catch (MenuUnmoveableException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		} catch (MenuItemNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		
		return "redirect:/menus";		
	}
	
	@GetMapping("/menus/movedown/{id}")
	public String moveMenuDown(@PathVariable(name = "id") Integer id, RedirectAttributes ra) {
		try {
			service.moveMenuDown(id);
			
			ra.addFlashAttribute("message", "The menu ID " + id + " has been moved down by one position.");
			
		} catch (MenuUnmoveableException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		} catch (MenuItemNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		
		return "redirect:/menus";		
	}	
}
