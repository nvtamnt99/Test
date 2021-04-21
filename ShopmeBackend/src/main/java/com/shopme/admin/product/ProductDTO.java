package com.shopme.admin.product;

import com.shopme.common.entity.Product;

public class ProductDTO {
	private String imagePath;
	private String name;
	private float price;
	private float cost;

	public ProductDTO() {
		
	}
	
	public ProductDTO(Product product) {
		this.imagePath = product.getMainImagePath();
		this.name = product.getName();
		this.price = product.getDiscountPrice();
		this.cost = product.getCost();
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	
}
