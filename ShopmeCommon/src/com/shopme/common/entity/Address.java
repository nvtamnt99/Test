package com.shopme.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "addresses")
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;	
	
	@Column(name = "phone_number")
	private String phoneNumber;
	
	@Column(name = "address_line1")
	private String addressLine1;
	
	@Column(name = "address_line2")
	private String addressLine2;
	
	private String city;
	private String state;
	
	@Column(name = "postal_code")
	private String postalCode;
	
	@Column(name = "default_address")
	private boolean defaultSelection;
	
	@ManyToOne
	@JoinColumn(name = "country_id")
	private Country country;
	
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	public String toString() {
		String address = firstName;
		
		if (lastName != null && !lastName.isEmpty()) address += " " + lastName;
		
		address += ", " + addressLine1;
		
		if (addressLine2 != null && !addressLine2.isEmpty()) address += ", " + addressLine2;
		
		address += ", " + city;
		
		if (state != null && !state.isEmpty()) address += ", " + state;
		
		address += ", " + country.getName();
		
		address += ". Postal code: " + postalCode;
		address += ". Phone number: " + phoneNumber;
		
		return address;
	}

	public boolean isDefaultSelection() {
		return defaultSelection;
	}

	public void setDefaultSelection(boolean defaultSelection) {
		this.defaultSelection = defaultSelection;
	}
	
	
}
