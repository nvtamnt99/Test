package com.shopme.site.security.oauth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.shopme.common.entity.AuthenticationProvider;
import com.shopme.common.entity.Customer;
import com.shopme.site.customer.CustomerServices;

//@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	@Autowired
	private CustomerServices customerService; 
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
		String email = oauth2User.getEmail();
		Customer customer = customerService.getCustomerByEmail(email);
		String name = oauth2User.getName();
		
		if (customer == null) {
			customerService.createNewCustomerAfterOAuthLoginSuccess(email, name, AuthenticationProvider.GOOGLE);
		} else {
			customerService.updateCustomerAfterOAuthLoginSuccess(customer, name, AuthenticationProvider.GOOGLE);
		}
		
		super.onAuthenticationSuccess(request, response, authentication);
	}

}
