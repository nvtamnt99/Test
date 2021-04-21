package com.shopme.admin.brand;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Brand;

public interface BrandRepository extends PagingAndSortingRepository<Brand, Integer> {

	@Query("SELECT b FROM Brand b WHERE CONCAT(b.id, ' ', b.name) LIKE %?1%")
	public Page<Brand> findAll(String keyword, Pageable pageable);

	public Brand findByName(String name);
	
	Long countById(Integer id);
}
