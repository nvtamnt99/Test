package com.shopme.site.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.shopme.common.entity.Product;

public interface ProductRepository extends PagingAndSortingRepository<Product, Integer> {
	@Query("SELECT p FROM Product p WHERE p.enabled=true AND (p.category.id=?1 OR p.category.allParentIds LIKE %?2%)")
	public Page<Product> listByCategory(Integer categoryId, String categoryIdMatch, Pageable pageable);

	@Query("SELECT p FROM Product p WHERE p.enabled=true AND p.brand.id=?1")
	public Page<Product> listByBrand(Integer brandId, Pageable pageable);
	
	public Product findByAlias(String alias);
	
	@Query(value = "SELECT * FROM products p WHERE p.enabled=true AND MATCH(name, alias, short_description, full_description) "
			+ "AGAINST (?1)", nativeQuery = true)
	public Page<Product> search(String keyword, Pageable pageable);
	
	@Query(value = "SELECT * FROM products p WHERE p.enabled=true AND MATCH(name, alias, short_description, full_description) "
			+ "AGAINST (?1)", nativeQuery = true)
	public List<Product> searchForRecommendation(String keyword);
	
	@Query("UPDATE Product p SET p.reviewCount = p.reviewCount + 1 WHERE p.id = ?1")
	@Modifying
	public void increaseReviewCount(Integer productId);
	
	@Query("UPDATE Product p SET p.averageRating = (SELECT AVG(r.rating) FROM Review r WHERE r.product.id = ?1) WHERE p.id = ?1")
	@Modifying
	public void updateAverageRating(Integer productId);	
	
}
