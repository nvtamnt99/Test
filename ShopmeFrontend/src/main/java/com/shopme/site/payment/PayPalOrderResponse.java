package com.shopme.site.payment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PayPalOrderResponse {
	private String id;
	
	@JsonProperty("gross_total_amount")
	private GrossTotalAmount grossTotalAmount;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public GrossTotalAmount getGrossTotalAmount() {
		return grossTotalAmount;
	}

	public void setGrossTotalAmount(GrossTotalAmount grossTotalAmount) {
		this.grossTotalAmount = grossTotalAmount;
	}
	
	public String getTotal() {
		return this.grossTotalAmount.getValue();
	}
	
	public boolean validate(String orderId, String totalAmount) {
		boolean orderIdMatched = this.id.equals(orderId);
		boolean totalAmountMatched = this.getTotal().equals(totalAmount);
				
		return orderIdMatched && totalAmountMatched;
	}

	@Override
	public String toString() {
		return "PayPalOrderResponse [id=" + id + ", totalAmout=" + getTotal() + "]";
	}		
}

@JsonIgnoreProperties(ignoreUnknown = true)	
class GrossTotalAmount {
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}