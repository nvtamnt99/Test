package com.shopme.site.menu;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Menu;
import com.shopme.common.entity.MenuType;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {

	public List<Menu> findByTypeAndEnabledOrderByPositionAsc(MenuType type, boolean enabled);
	
	public Menu findByAlias(String alias);
}
