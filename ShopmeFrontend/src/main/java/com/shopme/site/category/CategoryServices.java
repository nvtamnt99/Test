package com.shopme.site.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Category;

@Service
public class CategoryServices {

	@Autowired
	private CategoryRepository repo;
	
	public List<Category> listRootCategories() {
		return repo.listRootCategories();
	}
	
	public List<Category> listNoChildrenCategories() {
		List<Category> listNoChildrenCategories = new ArrayList<>();
		
		Iterable<Category> iterable = repo.findAllEnabled();
		
		iterable.forEach(category -> {
			Set<Category> children = category.getChildren();
			if (children == null || children.isEmpty()) {
				listNoChildrenCategories.add(category);
			}
		});
		
		return listNoChildrenCategories;
		
	}
	
	public Category getCategory(String alias) {
		return repo.findByAliasEnabled(alias);
	}
	
	public List<Category> getCategoryParents(Category child) {
		List<Category> parents = new ArrayList<Category>();
		Category parent = child.getParent();
		
		while (parent != null) {
			parents.add(0, parent);
			parent = parent.getParent();
		}		
		
		parents.add(child);
		
		return parents;
	}
}
