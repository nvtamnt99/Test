package com.shopme.common.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Category;

@Entity
@Table(name = "products")
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 256, unique = true, nullable = false)
	private String name;

	@Column(length = 256, unique = true, nullable = false)
	private String alias;
	
	@Column(name = "short_description", nullable = false)	
	private String shortDescription;
	
	@Column(name = "full_description", nullable = false)
	private String fullDescription;
	
	@Column(name = "main_image")
	private String mainImage;

	@Column(name = "created_time", updatable = false)	
	private Date createdTime;
	
	@Column(name = "updated_time")
	private Date updatedTime;

	private boolean enabled;
	
	@Column(name = "in_stock")
	private boolean inStock;

	@Column(name = "cost")
	private float cost;
	
	@Column(name = "list_price")
	private float price;
	
	@Column(name = "discount_percent")
	private float discountPercent;
	
	private float length;
	private float width;
	private float height;
	private float weight;

	@Column(name = "average_rating")
	private float averageRating;
	
	@Column(name = "review_count")
	private int reviewCount;
	
	@ManyToOne
	@JoinColumn(name = "brand_id")
	private Brand brand;
	
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ProductImage> images = new HashSet<>();
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductDetail> details = new ArrayList<>();

	public Product() {
	}

	public Product(Integer id) {
		this.id = id;
	}
	
	public Product(String categoryName) {
		this.category = new Category(categoryName);
	}
	
	public Product(Integer id, String name) {
		this.id = id;
		this.name = name;
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

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getFullDescription() {
		return fullDescription;
	}

	public void setFullDescription(String fullDescription) {
		this.fullDescription = fullDescription;
	}

	public String getMainImage() {
		return mainImage;
	}

	public void setMainImage(String mainImage) {
		this.mainImage = mainImage;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isInStock() {
		return inStock;
	}

	public void setInStock(boolean inStock) {
		this.inStock = inStock;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getDiscountPercent() {
		return discountPercent;
	}

	public void setDiscountPercent(float discountPercent) {
		this.discountPercent = discountPercent;
	}

	public Brand getBrand() {
		return brand;
	}

	public void setBrand(Brand brand) {
		this.brand = brand;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	@Transient
	public String getMainImagePath() {
		if (mainImage == null || id == null) return "/images/image-thumbnail.png";
		
		return "/product-images/" + id + "/" + mainImage;
	}
	
	public void addExtraImage(String imageName) {
		this.images.add(new ProductImage(imageName, this));
	}


	public Set<ProductImage> getImages() {
		return images;
	}

	public void setImages(Set<ProductImage> images) {
		this.images = images;
	}
	
	public boolean containsImageName(String imageName) {
		Iterator<ProductImage> iterator = images.iterator();
		
		while (iterator.hasNext()) {
			ProductImage image = iterator.next();
			if (image.getName().equals(imageName)) {
				return true;
			}
		}
		
		return false;
	}

	public List<ProductDetail> getDetails() {
		return details;
	}

	public void setDetails(List<ProductDetail> details) {
		this.details = details;
	}
	
	public void addDetail(String name, String value) {
		this.details.add(new ProductDetail(name, value, this));
	}
	
	public void addDetail(Integer id, String name, String value) {
		this.details.add(new ProductDetail(id, name, value, this));
	}
	
	public String getAlias() {
		return this.alias;
	}
	
	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Transient
	public float getDiscountPrice() {
		if (discountPercent > 0) {
			return this.price * ((100 - discountPercent) / 100);
		}
		
		return this.price;
	}
	
	@Transient
	public String getShortName() {
		if (name.length() > 70) {
			return name.substring(0, 70).concat("...");
		}
		return name;
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
		Product other = (Product) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Transient
	public String getURI() {
		return "/p/" + this.alias + "/";
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public float getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(float averageRating) {
		this.averageRating = averageRating;
	}

	public int getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(int reviewCount) {
		this.reviewCount = reviewCount;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	
}
