package com.shopme.common.entity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "orders")
public class Order {
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
	
	@Column(name = "postal_code")
	private String postalCode;

	private String city;
	private String state;
	private String country;
	
	private Date orderTime;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "payment_method")
	private PaymentMethod paymentMethod;
	
	@Column(name = "shipping_cost")
	private float shippingCost;
	private float cost;
	private float tax;
	private float subtotal;
	private float total;
	
	@Enumerated(EnumType.STRING)
	private OrderStatus status;
	
	@Column(name = "deliver_days")
	private int deliverDays;
	
	@Column(name = "deliver_date")
	private Date deliverDate;
	
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<OrderDetail> orderDetails = new HashSet<>();
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("updatedTime ASC")
	private List<OrderTrack> orderTracks = new ArrayList<>();

	public Order() {
		
	}
	
	public Order(Integer id, Date orderTime, float cost, float subtotal, float total) {
		this.id = id;
		this.orderTime = orderTime;
		this.cost = cost;
		this.subtotal = subtotal;
		this.total = total;
	}

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

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public float getShippingCost() {
		return shippingCost;
	}

	public void setShippingCost(float shippingCost) {
		this.shippingCost = shippingCost;
	}

	public float getTax() {
		return tax;
	}

	public void setTax(float tax) {
		this.tax = tax;
	}

	public float getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(float subtotal) {
		this.subtotal = subtotal;
	}

	public float getTotal() {
		return total;
	}

	public void setTotal(float total) {
		this.total = total;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Set<OrderDetail> getOrderDetails() {
		return orderDetails;
	}

	public void setOrderDetails(Set<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}
	
	public void copyShippingAddressFromCustomer(Customer customer) {
		this.firstName = customer.getFirstName();
		this.lastName = customer.getLastName();
		this.addressLine1 = customer.getAddressLine1();
		this.addressLine2 = customer.getAddressLine2();
		this.city = customer.getCity();
		this.country = customer.getCountryName();
		this.state = customer.getState();
		this.phoneNumber = customer.getPhoneNumber();
		this.postalCode = customer.getPostalCode();
	}
	
	public void copyShippingAddress(Address address) {
		this.firstName = address.getFirstName();
		this.lastName = address.getLastName();
		this.addressLine1 = address.getAddressLine1();
		this.addressLine2 = address.getAddressLine2();
		this.city = address.getCity();
		this.country = address.getCountry().getName();
		this.state = address.getState();
		this.phoneNumber = address.getPhoneNumber();
		this.postalCode = address.getPostalCode();
	}
	
	@Transient
	public String getDestination() {
		String destination = city;
		if (state != null && !state.isEmpty()) {
			destination += ", " + state;
		}
		
		destination += ", " + country;
		
		return destination;
	}

	public int getDeliverDays() {
		return deliverDays;
	}

	public void setDeliverDays(int deliverDays) {
		this.deliverDays = deliverDays;
	}

	public Date getDeliverDate() {
		return deliverDate;
	}

	public void setDeliverDate(Date deliverDate) {
		this.deliverDate = deliverDate;
	}

	public List<OrderTrack> getOrderTracks() {
		return orderTracks;
	}

	public void setOrderTracks(List<OrderTrack> orderTracks) {
		this.orderTracks = orderTracks;
	}
	
	@Transient
	public String getShippingAddress() {
		String address = firstName;
		
		if (lastName != null && !lastName.isEmpty()) address += " " + lastName;
		
		address += ", " + addressLine1;
		
		if (addressLine2 != null && !addressLine2.isEmpty()) address += ", " + addressLine2;
		
		address += ", " + city;
		
		if (state != null && !state.isEmpty()) address += ", " + state;
		
		address += ", " + country;
		
		address += ". Postal code: " + postalCode;
		address += ". Phone number: " + phoneNumber;
		
		return address;
	}

	@Transient
	public String getDeliverDateOnForm() {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormatter.format(this.deliverDate);
	}
	
	public void setDeliverDateOnForm(String dateString) {
		System.out.println("setDeliverDateOnForm: " + dateString);
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			this.deliverDate = dateFormatter.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}
	
	
}
