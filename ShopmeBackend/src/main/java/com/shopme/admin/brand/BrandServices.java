package com.shopme.admin.brand;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Brand;

@Service
public class BrandServices {
	public static final int BRANDS_PER_PAGE = 10;
	
	@Autowired
	private BrandRepository repo;
	
	public Brand save(Brand brand) {
		return repo.save(brand);
	}
	
	public List<Brand> listAll() {
		return (List<Brand>) repo.findAll(Sort.by("name").ascending());
	}
	
	public Page<Brand> listAll(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
	
		Pageable pageable = PageRequest.of(pageNum - 1, BRANDS_PER_PAGE, sort);
		
		if (keyword != null) {
			return repo.findAll(keyword, pageable);
		}
		
		return repo.findAll(pageable);
	}
	
	public void delete(Integer id) throws BrandNotFoundException {
		Long count = repo.countById(id);
		if (count == null || count == 0) {
			throw new BrandNotFoundException("Could not find any brand with ID " + id);
		}
		
		repo.deleteById(id);
	}
	
	public Brand get(Integer id) throws BrandNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new BrandNotFoundException("Could not find any brand with ID " + id);
		}
	}
	
	public boolean checkUnique(Integer id, String name) {
		boolean isNew = (id == null || id == 0);
		Brand brandByName = repo.findByName(name);
		
		if (isNew) {
			if (brandByName != null) {
				return false;
			}
		} else {
			if (brandByName != null && brandByName.getId() != id) {
				return false;
			}
		}
		
		return true;
	}
}
