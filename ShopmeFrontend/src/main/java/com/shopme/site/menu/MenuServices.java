package com.shopme.site.menu;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Article;
import com.shopme.common.entity.Menu;
import com.shopme.common.entity.MenuType;

@Service
public class MenuServices {

	@Autowired
	private MenuRepository menuRepo;
	
	public List<Menu> getHeaderMenuItems() {
		return menuRepo.findByTypeAndEnabledOrderByPositionAsc(MenuType.HEADER, true);
	}
	
	public List<Menu> getFooterMenuItems() {
		return menuRepo.findByTypeAndEnabledOrderByPositionAsc(MenuType.FOOTER, true);
	}	
	
	public Article getArticleBoundToMenu(String menuAlias) throws MenuNotFoundException {
		Menu menu = menuRepo.findByAlias(menuAlias);
		if (menu == null) {
			throw new MenuNotFoundException("No menu found with alias " + menuAlias);
		}
		
		return menu.getArticle();
	}
}
