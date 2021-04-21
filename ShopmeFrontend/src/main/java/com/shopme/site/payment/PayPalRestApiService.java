package com.shopme.site.payment;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.shopme.site.setting.PaymentSettingBag;
import com.shopme.site.setting.SettingServices;

@Component
public class PayPalRestApiService {

	@Autowired
	private SettingServices settingService;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public boolean validatePayPalOrder(String orderId, String totalAmount) throws PayPalApiException {
		
		PayPalOrderResponse orderResponse = getOrderDetails(orderId);
		System.out.println(orderResponse);
		
		return orderResponse.validate(orderId, totalAmount);
	}
	
	private PayPalOrderResponse getOrderDetails(String orderId) throws PayPalApiException {
		ResponseEntity<PayPalOrderResponse> response = makeRequest(orderId);
		HttpStatus statusCode = response.getStatusCode();
		
		if (!statusCode.equals(HttpStatus.OK)) {
			throwExceptionForNonOkResponse(statusCode);
		}
		
		return response.getBody();
	}

	private void throwExceptionForNonOkResponse(HttpStatus statusCode) throws PayPalApiException {
		String error = null;
		
		switch (statusCode) {
			case NOT_FOUND:
				error = "Order ID not found";
			case BAD_REQUEST:
				error = "Bad Request to PayPal Checkout API";
			case INTERNAL_SERVER_ERROR:
				error = "PayPal Server error";
			default:
				error = "Paypal returned Non-OK status code";
		}
		
		throw new PayPalApiException(error);
	}

	private ResponseEntity<PayPalOrderResponse> makeRequest(String orderId) 
			throws PayPalApiException {
		PaymentSettingBag settings = settingService.getPaymentSettings();
		String baseURL = settings.getURL();
		String orderDetailApiURL = baseURL + "/v1/checkout/orders";
		
		String clientId = settings.getClientID();
		String clientSecret = settings.getClientSecret();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.add("Accept-Language", "en_US");
		headers.setBasicAuth(clientId, clientSecret);
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
		
		String url = orderDetailApiURL + "/" + orderId;
		
		try {
			return restTemplate.exchange(
					url, HttpMethod.GET, request, PayPalOrderResponse.class);
		} catch (HttpClientErrorException ex) {
			throw new PayPalApiException("PayPal returned an error.");
		}
		
		
	}	
}
