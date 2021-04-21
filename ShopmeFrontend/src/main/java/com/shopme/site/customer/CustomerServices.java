package com.shopme.site.customer;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.AuthenticationProvider;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.site.security.CustomerUserDetails;
import com.shopme.site.security.oauth.CustomOAuth2User;

import net.bytebuddy.utility.RandomString;

@Service
@Transactional
public class CustomerServices {

	@Autowired CustomerRepository customerRepo;
	
	@Autowired CountryRepository countryRepo;
	
	@Autowired PasswordEncoder passwordEncoder;
	
	public List<Country> listAllCountries() {
		return countryRepo.findAllByOrderByNameAsc();
	}
	
	public void registerCustomer(Customer customer) {
		encodePassword(customer);		
		customer.setCreatedTime(new Date());
		customer.setEnabled(false);
		customer.setAuthProvider(AuthenticationProvider.LOCAL);
		
		String randomCode = RandomString.make(64);
		customer.setVerificationCode(randomCode);
		
		customerRepo.save(customer);
	}
	
	public void createNewCustomerAfterOAuthLoginSuccess(String email, String name, AuthenticationProvider provider) {
		Customer customer = new Customer();
		customer.setEmail(email);
		customer.setEnabled(true);
		customer.setCreatedTime(new Date());
		customer.setFirstName(name);
		customer.setAuthProvider(provider);
		
		
		customerRepo.save(customer);
	}
	
	public void updateCustomerAfterOAuthLoginSuccess(Customer customer, String name, AuthenticationProvider provider) {
		customer.setFirstName(name);
		customer.setAuthProvider(provider);		
		customerRepo.save(customer);
	}	
	
	public void updateCustomer(Customer customer) {
		Customer existCustomer = customerRepo.findById(customer.getId()).get();
		
		if (existCustomer.getAuthProvider().equals(AuthenticationProvider.LOCAL)) {		
			if (customer.getPassword().isEmpty()) {
				customer.setPassword(existCustomer.getPassword());
			} else {
				encodePassword(customer);
			}
		}
		
		customer.setAuthProvider(existCustomer.getAuthProvider());
		customer.setEnabled(true);
		
		customerRepo.save(customer);
	}
	
	private void encodePassword(Customer customer) {
		String encodedPassword = passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodedPassword);		
	}
	
	public boolean isEmailUnique(String email) {
		Customer existCustomer = customerRepo.findByEmail(email);
		return existCustomer == null;
	}	
	
	public Customer getCustomerByEmail(String email) {
		return customerRepo.findByEmail(email);
	}
	
	public boolean verify(String verificationCode) {
		Customer customer = customerRepo.findByVerificationCode(verificationCode);
		
		if (customer == null || customer.isEnabled()) {
			return false;
		} else {
			customerRepo.enable(customer.getId());
			return true;
		}
	}
	
	public Customer getCurrentlyLoggedInCustomer(Authentication authentication) {
		if (authentication == null) return null;
		
		Customer customer = null;
		Object principal = authentication.getPrincipal();
		
		if (principal instanceof CustomerUserDetails) {
			customer = ((CustomerUserDetails) principal).getCustomer();
		} else if (principal instanceof CustomOAuth2User) {
			String email = ((CustomOAuth2User) principal).getEmail();
			customer = getCustomerByEmail(email);			
		}
		
		return customer;
	}
	
	public void updateResetPasswordToken(String token, String email) throws CustomerNotFoundException {
		Customer customer = customerRepo.findByEmail(email);
		if (customer != null) {
			customer.setResetPasswordToken(token);
			customerRepo.save(customer);
		} else {
			throw new CustomerNotFoundException("Could not find any customer with the email " + email);
		}
	}
	
	public Customer getByResetPasswordToken(String token) {
		return customerRepo.findByResetPasswordToken(token);
	}
	
	public void updatePassword(Customer customer, String newPassword) {
		String encodedPassword = passwordEncoder.encode(newPassword);
		customer.setPassword(encodedPassword);
		
		customer.setResetPasswordToken(null);
		customerRepo.save(customer);
	}
}
