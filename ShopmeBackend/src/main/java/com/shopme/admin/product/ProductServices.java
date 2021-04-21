package com.shopme.admin.product;

import java.util.Date;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Product;

@Service
@Transactional
public class ProductServices {

	public static final int PRODUCTS_PER_PAGE = 5;
	
	@Autowired
	private ProductRepository repo;
	
	public Product save(Product product) {
		if (product.getId() == null) {
			product.setCreatedTime(new Date());
		}		
		
		if (product.getAlias() == null || product.getAlias().isEmpty()) {
			String defaultAlias = product.getName().replaceAll(" ", "-");
			product.setAlias(defaultAlias);			
		} else {
			product.setAlias(product.getAlias().replaceAll(" ", "-"));
		}
		
		product.setUpdatedTime(new Date());
		
		return repo.save(product);
	}
	
	public void saveProductPrice(Product productInForm) {
		Product productInDB = repo.findById(productInForm.getId()).get();
		
		productInDB.setPrice(productInForm.getPrice());
		productInDB.setCost(productInForm.getCost());
		productInDB.setDiscountPercent(productInForm.getDiscountPercent());
		
		repo.save(productInDB);
		
	}
	
	public Page<Product> listAll(int pageNum, String sortField, String sortDir, String keyword, Integer categoryId) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE, sort);
		
		if (keyword != null && !keyword.isEmpty()) {
			if (categoryId != null && categoryId > 0) {
				String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
				return repo.searchInCategory(categoryId, categoryIdMatch, keyword, pageable);
			}
			return repo.findAll(keyword, pageable);
		}
		
		if (categoryId != null && categoryId > 0) {
			String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
			return repo.findAllInCategory(categoryId, categoryIdMatch, pageable);
		}		
		
		return repo.findAll(pageable);
	}
	
	public Page<Product> searchProducts(int pageNum, String keyword) {
		Sort sort = Sort.by("name").ascending();
		Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE, sort);
		
		return repo.searchProductsByName(keyword, pageable);
	}
	
	public Product get(Integer id) throws ProductNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new ProductNotFoundException("Could not find any product with ID " + id);
		}
	}
	
	public void delete(Integer id) throws ProductNotFoundException {
		Long count = repo.countById(id);
		if (count == null || count == 0) {
			throw new ProductNotFoundException("Could not find any product with ID " + id);
		}
		
		repo.deleteById(id);
	}
	
	public boolean checkUnique(Integer id, String name) {
		boolean isNew = (id == null || id == 0);
		Product productByName = repo.findByName(name);
		
		if (isNew) {
			if (productByName != null) {
				return false;
			}
		} else {
			if (productByName != null && productByName.getId() != id) {
				return false;
			}
		}
		
		return true;
	}
	
	public void updateProductEnabledStatus(Integer id, boolean enabled) {
		repo.updateEnabledStatus(id, enabled);
	}	
}
