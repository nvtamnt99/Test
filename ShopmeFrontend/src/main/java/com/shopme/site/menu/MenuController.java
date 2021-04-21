package com.shopme.site.menu;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopme.common.entity.Article;

@Controller
public class MenuController {

	@Autowired
	private MenuServices service;
	
	@GetMapping("/m/{alias}")
	public String viewMenu(@PathVariable(name = "alias") String alias, Model model,
			HttpServletResponse response) throws IOException {
		try {
			Article article = service.getArticleBoundToMenu(alias);
			model.addAttribute("pageTitle", article.getTitle());
			model.addAttribute("article", article);
			
		} catch (MenuNotFoundException ex) {
			return "error/404";
		}
		
		return "article";
	}
}
