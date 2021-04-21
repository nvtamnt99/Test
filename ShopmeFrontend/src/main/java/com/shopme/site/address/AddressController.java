package com.shopme.site.address;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.site.customer.CustomerServices;

@Controller
public class AddressController {

	@Autowired
	private AddressServices addressService;
	
	@Autowired
	private CustomerServices customerService;	
	
	@GetMapping("/customer/address")
	public String listAddresses(Model model, @AuthenticationPrincipal Authentication authentication) {
		Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
		// refresh customer's info from database
		customer = customerService.getCustomerByEmail(customer.getEmail());
		
		List<Address> listAddresses = addressService.listAddressesOf(customer);
		Address defaultAddress = addressService.getDefaultAddressOf(customer);
		Integer defaultAddressId = 0;	// customer's address
		
		if (defaultAddress != null) {
			defaultAddressId = defaultAddress.getId();
		}
		
		model.addAttribute("listAddresses", listAddresses);
		model.addAttribute("defaultAddressId", defaultAddressId);
		model.addAttribute("customer", customer);
		model.addAttribute("pageTitle", "Choose a Shipping Address");
		
		return "address/addresses";
	}
	
	@GetMapping("/customer/address/new")
	public String newAddress(Model model, @AuthenticationPrincipal Authentication authentication) {
		Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
		List<Country> countries = customerService.listAllCountries();
		
		model.addAttribute("listCountries", countries);
		
		model.addAttribute("customer", customer);
		model.addAttribute("pageTitle", "Create New Address");
		model.addAttribute("address", new Address());
		
		return "address/address_form";		
	}
	
	@PostMapping("/customer/address/save")
	public String saveAddress(Address address, HttpServletRequest request,
			@AuthenticationPrincipal Authentication authentication) {
		Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
		String redirectOption = request.getParameter("redirect");
		String redirectURL = "redirect:/address";
		
		address.setCustomer(customer);
		
		if ("checkout".equals(redirectOption)) {
			address.setDefaultSelection(true);
			redirectURL = "redirect:/checkout";
		}
		
		addressService.save(address, customer);
		
		return redirectURL;
		
	}
	
	@GetMapping("/customer/address/choose/{id}")
	public String chooseDefaultAddress(@PathVariable(name = "id") Integer addressId,
			HttpServletRequest request,
			@AuthenticationPrincipal Authentication authentication) {
		Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
		addressService.setDefaultAddress(addressId, customer);		
		
		String redirectOption = request.getParameter("redirect");
		
		if ("checkout".equals(redirectOption)) {
			return "redirect:/checkout";
		}
		
		return "redirect:/customer/address";
	}
	
	@GetMapping("/customer/address/edit/{id}")
	public String editAddress(@PathVariable(name = "id") Integer addressId,
			Model model,
			@AuthenticationPrincipal Authentication authentication) {
		
			Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
			Address address = addressService.get(addressId, customer);
			
			if (address == null) {
				model.addAttribute("title", "Edit Address");
				model.addAttribute("message", "Could not find any address with ID " + addressId);
				return "message";
			}
			
			List<Country> countries = customerService.listAllCountries();
			
			model.addAttribute("listCountries", countries);
						model.addAttribute("address", address);
			model.addAttribute("pageTitle", "Edit Address (ID: " + addressId + ")");
			
			return "address/address_form";
	}

	@GetMapping("/customer/address/delete/{id}")
	public String deleteAddress(@PathVariable(name = "id") Integer addressId,
			Model model,
			@AuthenticationPrincipal Authentication authentication) {
		
			Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
			addressService.delete(addressId, customer);
			
			return "redirect:/customer/address";
	}	
	
}
