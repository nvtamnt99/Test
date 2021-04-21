package com.shopme.admin.menu;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Menu;
import com.shopme.common.entity.MenuType;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {
	@Query("UPDATE Menu m SET m.enabled = ?2 WHERE m.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);
	
	public List<Menu> findAllByOrderByTypeAscPositionAsc();
	
	public List<Menu> findByType(MenuType type);
	
	public List<Menu> findByTypeOrderByPositionAsc(MenuType type);
	
	public Long countByType(MenuType type); 
}
