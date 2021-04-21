package com.shopme.admin.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.shopme.common.entity.Category;

public interface CategoryRepository extends PagingAndSortingRepository<Category, Integer> {

	@Query("SELECT c FROM Category c WHERE c.parent is null")
	public Page<Category> findAll(Pageable pageable);
	
	@Query("SELECT c FROM Category c WHERE c.name LIKE %?1%")
	public Page<Category> search(String keyword, Pageable pageable);
	
	public Category findByName(String name);
	
	public Category findByAlias(String alias);
	
	@Query("UPDATE Category c SET c.enabled = ?2 WHERE c.id = ?1")
	@Modifying
	public void updateEnabledStatus(@Param("id") Integer id, @Param("enabled") boolean enabled);	
	
	Long countById(Integer id);
}
