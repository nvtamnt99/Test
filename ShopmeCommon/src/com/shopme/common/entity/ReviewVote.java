package com.shopme.common.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "reviews_votes")
public class ReviewVote {
	public static final int UP = 1;
	public static final int DOWN = -1;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer  id;
	
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;
	
	@ManyToOne
	@JoinColumn(name = "review_id")
	private Review review;
	
	private int votes;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Review getReview() {
		return review;
	}

	public void setReview(Review review) {
		this.review = review;
	}

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}
	
	public void upVote() {
		this.votes = UP;
	}
	
	public void downVote() {
		this.votes = DOWN;
	}

	@Transient
	public boolean isUpvoted() {
		return this.votes == UP;
	}
	
	@Transient
	public boolean isDownvoted() {
		return this.votes == DOWN;
	}
}
