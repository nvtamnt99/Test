package com.shopme.admin.menu;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Menu;

@Service
@Transactional
public class MenuServices {

	@Autowired
	private MenuRepository repo;
	
	public List<Menu> listAll() {
		return repo.findAllByOrderByTypeAscPositionAsc();
	}
	
	public void save(Menu menu) {
		if (menu.getId() == null) {
			setPositionForNewMenu(menu);
		} else {
			setPositionForEditedMenu(menu);
		}
		repo.save(menu);
	}
	
	private void setPositionForEditedMenu(Menu menu) {
		Menu existMenu = repo.findById(menu.getId()).get();
		if (!existMenu.getType().equals(menu.getType())) {
			setPositionForNewMenu(menu);
		}
	}

	private void setPositionForNewMenu(Menu newMenu) {
		Long newPosition = repo.countByType(newMenu.getType()) + 1;
		newMenu.setPosition(newPosition.intValue());		
	}
	
	public Menu get(Integer id) throws MenuItemNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new MenuItemNotFoundException("Could not find any menu item with ID " + id);
		}
	}
	
	public void enable(Integer id) {
		repo.updateEnabledStatus(id, true);
	}
	
	public void disable(Integer id) {
		repo.updateEnabledStatus(id, false);
	}
	
	public void delete(Integer id) {
		repo.deleteById(id);
	}

	public void moveMenuUp(Integer id) throws MenuUnmoveableException, MenuItemNotFoundException {
		Optional<Menu> findById = repo.findById(id);
		if (!findById.isPresent()) {
			throw new MenuItemNotFoundException("Could not find any menu item with ID " + id);
		}
		
		Menu currentMenu = findById.get();
		List<Menu> allMenusOfSameType = repo.findByTypeOrderByPositionAsc(currentMenu.getType());
		int currentMenuIndex = allMenusOfSameType.indexOf(currentMenu);
		
		if (currentMenuIndex == 0) {
			throw new MenuUnmoveableException("The menu ID " + id + " is already in the first position");
		}
		
		int previousMenuIndex = currentMenuIndex - 1;
		Menu previousMenu = allMenusOfSameType.get(previousMenuIndex);
		
		currentMenu.setPosition(previousMenuIndex + 1);
		allMenusOfSameType.set(previousMenuIndex, currentMenu);
		
		previousMenu.setPosition(currentMenuIndex + 1);
		allMenusOfSameType.set(currentMenuIndex, previousMenu);
		
		repo.saveAll(Arrays.asList(currentMenu, previousMenu));
		
		
	}
	
	public void moveMenuDown(Integer id) throws MenuUnmoveableException, MenuItemNotFoundException {
		Optional<Menu> findById = repo.findById(id);
		if (!findById.isPresent()) {
			throw new MenuItemNotFoundException("Could not find any menu item with ID " + id);
		}
		
		Menu currentMenu = findById.get();
		List<Menu> allMenusOfSameType = repo.findByTypeOrderByPositionAsc(currentMenu.getType());
		int currentMenuIndex = allMenusOfSameType.indexOf(currentMenu);
		
		if (currentMenuIndex == allMenusOfSameType.size() - 1) {
			throw new MenuUnmoveableException("The menu ID " + id + " is already in the last position");
		}
		
		int nextMenuIndex = currentMenuIndex + 1;
		Menu nextMenu = allMenusOfSameType.get(nextMenuIndex);
		
		currentMenu.setPosition(nextMenuIndex + 1);
		allMenusOfSameType.set(nextMenuIndex, currentMenu);
		
		nextMenu.setPosition(currentMenuIndex + 1);
		allMenusOfSameType.set(currentMenuIndex, nextMenu);
		
		repo.saveAll(Arrays.asList(currentMenu, nextMenu));
		
		
	}	
}
