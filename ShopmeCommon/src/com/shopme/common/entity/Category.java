package com.shopme.common.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "categories")
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 128, nullable = false, unique = true)
	private String name;

	private boolean enabled;

	@Column(length = 64, nullable = false, unique = true)
	private String alias;
	
	private String image;
	
	@Column(length = 256, nullable = true)
	private String allParentIds;
	
	@OneToOne
	@JoinColumn(name = "parent_id")
	private Category parent;
	
	@OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
	@OrderBy("name asc")
	private Set<Category> children = new HashSet<>();
	
	@ManyToMany(mappedBy = "categories")
	private Set<Brand> brands = new HashSet<>();
	
	public Category() {
	}

	public Category(boolean enabled) {
		this.enabled = enabled;
	}

	public Category(String name) {
		this.name = name;
	}

	public Category(Integer id) {
		this.id = id;
	}

	public Category(String name, boolean enabled, Category parent) {
		this.name = name;
		this.enabled = enabled;
		this.parent = parent;
	}
	
	public Category(Category copy) {
		this.id = copy.getId();
		this.name = copy.getName();
		this.enabled = copy.isEnabled();
		this.alias = copy.getAlias();
		this.image = copy.getImage();
	}	
	
	public Category(Integer id, String name, boolean enabled, String alias, String image) {
		this.id = id;
		this.name = name;
		this.enabled = enabled;
		this.alias = alias;
		this.image = image;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public Set<Category> getChildren() {
		return children;
	}

	public void setChildren(Set<Category> children) {
		this.children = children;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Transient
	public String getImagePath() {
		if (image == null || id == null) return "/images/image-thumbnail.png";
		return "/category-images/" + id + "/" + image;
	}
	
	@Transient
	public String getOriginalName() {
		return this.name.replaceAll("-", "");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Set<Brand> getBrands() {
		return brands;
	}

	public void setBrands(Set<Brand> brands) {
		this.brands = brands;
	}

	public String getAllParentIds() {
		return allParentIds;
	}

	public void setAllParentIds(String allParentIds) {
		this.allParentIds = allParentIds;
	}		
	

	public boolean isHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}


	@Transient
	private boolean hasChildren;
}
