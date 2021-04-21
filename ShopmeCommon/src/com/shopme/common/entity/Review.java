package com.shopme.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "reviews")
public class Review {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String headline;
	private String comment;
	private int rating;
	private int votes;
	
	@Column(name = "review_time", updatable = false)
	private Date reviewTime;
	
	@ManyToOne
	@JoinColumn(name = "product_id", updatable = false)
	private Product product;
	
	@ManyToOne
	@JoinColumn(name = "customer_id", updatable = false)
	private Customer customer;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getHeadline() {
		return headline;
	}

	public void setHeadline(String headline) {
		this.headline = headline;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}

	public Date getReviewTime() {
		return reviewTime;
	}

	public void setReviewTime(Date reviewTime) {
		this.reviewTime = reviewTime;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@Transient
	public String getProductName() {
		return product.getName();
	}
	
	@Transient
	public String getCustomerName() {
		return customer.getFullName();
	}
	
	public boolean isUpvotedByCurrentCustomer() {
		return isUpvotedByCurrentCustomer;
	}

	public void setUpvotedByCurrentCustomer(boolean isUpvotedByCurrentCustomer) {
		this.isUpvotedByCurrentCustomer = isUpvotedByCurrentCustomer;
	}

	
	public boolean isDownvotedByCurrentCustomer() {
		return isDownvotedByCurrentCustomer;
	}

	public void setDownvotedByCurrentCustomer(boolean isDownvotedByCurrentCustomer) {
		this.isDownvotedByCurrentCustomer = isDownvotedByCurrentCustomer;
	}

	@Transient
	private boolean isUpvotedByCurrentCustomer;
	
	@Transient	
	private boolean isDownvotedByCurrentCustomer;

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
		Review other = (Review) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}	
	
	
}
