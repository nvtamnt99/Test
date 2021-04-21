package com.shopme.site.product;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Product;

@Service
public class ProductServices {
	public static final int PRODUCTS_PER_PAGE = 10;
	public static final int SEARCH_RESULT_PER_PAGE = 10;
	
	@Autowired
	private ProductRepository repo;
	
	public Page<Product> listByCategory(int pageNum, Integer categoryId) {
		String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
		
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
		
		return repo.listByCategory(categoryId, categoryIdMatch, pageable);
	}
	
	public Page<Product> listByBrand(int pageNum, Integer brandId) {
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
		
		return repo.listByBrand(brandId, pageable);
	}	
	
	public Product getProduct(String alias) throws ProductNotFoundException {
		Product product = repo.findByAlias(alias);
		if (product == null) {
			throw new ProductNotFoundException();
		}
		
		return product;
	}
	
	public Product get(Integer id) throws ProductNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new ProductNotFoundException("Could not find any product with ID " + id);
		}
	}
	
	public Page<Product> search(String keyword, int pageNum) {
		Pageable pageable = PageRequest.of(pageNum - 1, SEARCH_RESULT_PER_PAGE);		
		return repo.search(keyword, pageable);
	}
	
	public List<Product> getRecommendedList(Product currentProduct) {
		List<Product> recommendedProducts = new ArrayList<>();
		
		Integer brandId = currentProduct.getBrand().getId();
		Integer categoryId = currentProduct.getCategory().getId();
		
		String brand = currentProduct.getBrand().getName();
		String category = currentProduct.getCategory().getName();
		String keyword = brand + " " + category;
		
		List<Product> searchResult = repo.searchForRecommendation(keyword);
		
		// filter out current product
		searchResult.remove(currentProduct);
		
		// choose only products in the same category or of same brand
		for (Product product : searchResult) {
			if (product.getCategory().equals(currentProduct.getCategory()) ||
					product.getBrand().equals(currentProduct.getBrand())) {
				recommendedProducts.add(product);
			}
		}
		
		
		return recommendedProducts;
	}
}
