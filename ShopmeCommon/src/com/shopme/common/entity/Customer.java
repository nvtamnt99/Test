package com.shopme.common.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "customers")
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String email;
	private String password;
	
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
	
	@Column(name = "created_time", updatable = false)
	private Date createdTime;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "auth_provider")
	public AuthenticationProvider authProvider;
	
	private boolean enabled;
	
	@Column(name = "verification_code", updatable = false)
	private String verificationCode;
	
	@Column(name = "reset_password_token")
	private String resetPasswordToken;
		
	@ManyToOne
	@JoinColumn(name = "country_id")
	private Country country;

	public Customer() {
	}

	public Customer(Integer id) {
		this.id = id;
	}

	public Customer(String email, String password, String fullName, String phoneNumber, 
			String addressLine1, String addressLine2, String city, String state, 
			String postalCode, boolean enabled) {
		this.email = email;
		this.password = password;
		this.firstName = fullName;
		this.phoneNumber = phoneNumber;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.city = city;
		this.state = state;
		this.postalCode = postalCode;
		this.enabled = enabled;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
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

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	@Transient
	public String getCountryName() {
		return country != null ? country.getName() : "";
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	@Transient
	public String getFullName() {
		if (lastName == null) return firstName;
		return firstName + " " + lastName;
	}

	public AuthenticationProvider getAuthProvider() {
		return authProvider;
	}

	public void setAuthProvider(AuthenticationProvider authProvider) {
		this.authProvider = authProvider;
	}

	public String getResetPasswordToken() {
		return resetPasswordToken;
	}

	public void setResetPasswordToken(String resetPasswordToken) {
		this.resetPasswordToken = resetPasswordToken;
	}
		
	@Transient
	public String getAddress() {
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
	
}
