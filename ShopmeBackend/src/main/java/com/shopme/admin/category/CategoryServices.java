package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopme.common.entity.Category;

@Service
@Transactional
public class CategoryServices {
	public static final int CATS_PER_PAGE = 4;
	
	@Autowired
	private CategoryRepository repo;

	public List<Category> listAll() {
		return listAll(-1, null, "name", "asc", null);
	}
	
	public List<Category> listAll(int pageNum, CategoryPageInfo pageInfo, 
			String sortField, String sortDir, String keyword) {
		if (keyword != null && !keyword.isEmpty()) {
			Sort sort = Sort.by(sortField);
			sort = sortDir.equals("asc") ? sort.ascending() : sort.descending(); 
			
			Pageable pageble = PageRequest.of(pageNum - 1, CATS_PER_PAGE, sort);
			Page<Category> localPage = repo.search(keyword, pageble);
			pageInfo.setTotalElements(localPage.getTotalElements());
			pageInfo.setTotalPages(localPage.getTotalPages());
			
			List<Category> searchResult = localPage.getContent();
			
			for (Category cat : searchResult) {
				cat.setHasChildren(cat.getChildren().size() > 0);
			}
			
			return searchResult;
		}
		
		List<Category> hierarchicalCategories = null;
		
		if (pageNum > 0) {
			Sort sort = Sort.by(sortField);
			sort = sortDir.equals("asc") ? sort.ascending() : sort.descending(); 
			
			Pageable pageble = PageRequest.of(pageNum - 1, CATS_PER_PAGE, sort);
			Page<Category> localPage = repo.findAll(pageble);
			hierarchicalCategories = localPage.getContent();
			
			pageInfo.setTotalElements(localPage.getTotalElements());
			pageInfo.setTotalPages(localPage.getTotalPages());
			
			
		} else {
			hierarchicalCategories = (List<Category>) repo.findAll(Sort.by("name").ascending());
		}
		
		List<Category> flatCategories = new ArrayList<>();
		for (Category cat : hierarchicalCategories) {
			if (cat.getParent() != null) continue;
			
			Set<Category> children = sortChildren(cat.getChildren(), sortDir);
			
			Category parentCat = new Category(cat);
			parentCat.setHasChildren(children.size() > 0);
			
			flatCategories.add(parentCat);
			
			for (Category child : children) {	
				
				String subLevelName = "--" + child.getName();
				Category copyCat = new Category(child);
				copyCat.setName(subLevelName);
				copyCat.setHasChildren(child.getChildren().size() > 0);
				flatCategories.add(copyCat);	
				
				addChildCategory(child, flatCategories, 1, sortDir);
			}			
		}
		
		
		return flatCategories;
	}
	
	private void addChildCategory(Category parent, List<Category> flatCategories, int subLevel, String sortDir) {
		Set<Category> children = sortChildren(parent.getChildren(), sortDir);
		
		int newSubLevel = subLevel + 1;
		
		for (Category child : children) {
			String prefix = "";
			for (int i = 1; i <=
					newSubLevel; i++) {
				prefix += "--";
			}
			String subLevelName = prefix + child.getName();
			
			Category copyCat = new Category(child);
			copyCat.setName(subLevelName);
			copyCat.setHasChildren(child.getChildren().size() > 0);
			flatCategories.add(copyCat);
			
			addChildCategory(child, flatCategories, newSubLevel, sortDir);
		}		
	}
	
	private SortedSet<Category> sortChildren(Set<Category> children, String sortDir) {
		SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {

			@Override
			public int compare(Category cat1, Category cat2) {
				if (sortDir.equals("asc")) {
					return cat1.getName().compareTo(cat2.getName());
				} else {
					return cat2.getName().compareTo(cat1.getName());
				}
			}
		});
		sortedChildren.addAll(children);
		
		return sortedChildren;
	}
	
	public List<Category> search(String keyword) {
		Pageable pageable = PageRequest.of(0, 5);
		Page<Category> searchResult = repo.search(keyword, pageable);
		List<Category> resultContent = searchResult.getContent();
		
		List<Category> flatCategories = new ArrayList<>();
		
		for (Category cat : resultContent) {
			Category childCatDTO = new Category(cat);
			Category catDTO = childCatDTO;
			
			flatCategories.add(childCatDTO);

			Category parentCat = cat.getParent();
				
			int parentCount = 0;
			
			while (parentCat != null) {
				parentCount++;
				int previousCatIndex = flatCategories.indexOf(childCatDTO);

				Category parentCatDTO = new Category(parentCat);
				
				if (!flatCategories.contains(parentCatDTO)) {
					flatCategories.add(previousCatIndex, parentCatDTO);
				}

				parentCat = parentCat.getParent();
				childCatDTO = parentCatDTO;
			}
			
			String identation = "";
			for (int i = 0; i < parentCount; i++) identation += "--";
			catDTO.setName(identation + catDTO.getName());
		}
		
		int subLevel = 0;
		boolean foundMatch = false;
		// skip 1st element which is always the root parent
		
		for (int j = 1; j < flatCategories.size(); j++) {
			Category catDTO = flatCategories.get(j);
			if (!catDTO.getName().toLowerCase().contains(keyword.toLowerCase())) {
				if (foundMatch) {
					subLevel = 0;
					foundMatch = false;
					continue;
				}
				subLevel++;
				String identation = "";
				for (int i = 0; i < subLevel; i++) identation += "--";
				catDTO.setName(identation + catDTO.getName());
				
				foundMatch = false;
			} else {
				foundMatch = true;
			}
		}
		
		return flatCategories;
	}
	
	public Category save(Category category) {
		Category parent = category.getParent();
		
		if (parent != null) {
			String allParentIds = parent.getAllParentIds() == null ? "-" : parent.getAllParentIds();
			allParentIds += String.valueOf(parent.getId()) + "-";
			category.setAllParentIds(allParentIds);
		}
		
		return repo.save(category);
	}
	
	public Category get(Integer id) throws CategoryNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new CategoryNotFoundException("Cound not find any category with ID " + id);
		}
	}
	
	public void delete(Integer id) throws CategoryNotFoundException {
		Long count = repo.countById(id);
		if (count == null || count == 0) {
			throw new CategoryNotFoundException("Cound not find any category with ID " + id);
		}
		
		repo.deleteById(id);
	}
	
	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);
		
		Category categoryByName = repo.findByName(name);
		
		if (isCreatingNew) {
			if (categoryByName != null) {
				return "DuplicatedName";
			} else {
				Category categoryByAlias = repo.findByAlias(alias);
				if (categoryByAlias !=  null) {
					return "DuplicatedAlias";
				}
			}
		} else {
			if (categoryByName != null && categoryByName.getId() != id) {
				return "DuplicatedName";
			}
			
			Category categoryByAlias = repo.findByAlias(alias);
			if (categoryByAlias !=  null && categoryByAlias.getId() != id) {
				return "DuplicatedAlias";
			}			
		}
		
		return "OK";		
	}
	
	public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
		repo.updateEnabledStatus(id, enabled);
	}	
}
