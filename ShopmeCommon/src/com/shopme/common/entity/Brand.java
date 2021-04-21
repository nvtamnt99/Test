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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "brands")
public class Brand {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length = 45, nullable = false, unique = true)
	private String name;

	@Column(length = 45, nullable = false)
	private String logo;
	
	@ManyToMany(cascade = CascadeType.PERSIST)
	@OrderBy("name asc")
	@JoinTable(
			name = "brands_categories",
			joinColumns = @JoinColumn(name = "brand_id"),
			inverseJoinColumns = @JoinColumn(name = "category_id")
			)
	private Set<Category> categories = new HashSet<>();
	
	public Brand() {
	}	
	
	public Brand(String name) {
		this.name = name;
	}
	
	public Brand(String name, String logo) {
		this.name = name;
		this.logo = logo;
	}
	
	public Brand(Integer id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public Brand(Integer id) {
		this.id = id;
	}
	
	public Brand(Integer id, String name, String logo) {
		super();
		this.id = id;
		this.name = name;
		this.logo = logo;
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

	public Set<Category> getCategories() {
		return categories;
	}

	public void setCategories(Set<Category> categories) {
		this.categories = categories;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public void addCategory(Category category) {
		this.categories.add(category);
	}
	
	@Transient
	public String getLogoImagePath() {
		if (logo == null || id == null) return "/images/image-thumbnail.png";
		
		return "/brand-logos/" + id + "/" + logo;
	}
	
	@Transient
	public String getCategoriesNames() {
		String names = "";
		
		for (Category cat : categories) {
			names += cat.getName() + ", ";
		}
		
		if (!names.isEmpty()) {
			names = names.substring(0, names.length() - 2);
		}
		
		return names;
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
		Brand other = (Brand) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
}
