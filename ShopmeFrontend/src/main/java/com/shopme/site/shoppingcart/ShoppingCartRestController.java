package com.shopme.site.shoppingcart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.Customer;
import com.shopme.site.customer.CustomerServices;

@RestController
public class ShoppingCartRestController {

	@Autowired
	private CustomerServices customerService;
	
	@Autowired
	private ShoppingCartServices cartService;
	
	@PostMapping("/cart/add/{pid}/{qty}")
	public String addProductToCart(
			@PathVariable(name = "pid") Integer productId,
			@PathVariable(name = "qty") Integer quantity,
			@AuthenticationPrincipal Authentication authentication) {
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "You must login to add this product to cart.";
		}
		
		Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
		if (customer == null) {
			return "You don't have permission to add this product to cart.";
		}		
		
		Integer addedQuantity = cartService.addProduct(productId, quantity, customer);
		
		return addedQuantity + " item(s) of this product were added to your shopping cart.";
	}
	
	@PostMapping("/cart/remove/{pid}")
	public String removeProductFromCart(@PathVariable(name = "pid") Integer productId,
			@AuthenticationPrincipal Authentication authentication) {
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "You must login to remove this product from cart.";
		}
		
		Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
		if (customer == null) {
			return "You don't have permission to remove this product from cart.";
		}		
		
		cartService.removeProduct(productId, customer);
		
		return "The product has been removed from your shopping cart.";
	}
	
	@PostMapping("/cart/update/{pid}/{qty}")
	public String updateQuantity(
			@PathVariable(name = "pid") Integer productId,
			@PathVariable(name = "qty") Integer quantity,
			@AuthenticationPrincipal Authentication authentication) {

		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return "You must login to change quantity.";
		}
		
		Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
		if (customer == null) {
			return "You don't have permission to change quantity.";
		}

		float newSubtotal = cartService.updateQuantity(productId, quantity, customer);
		
		return String.valueOf(newSubtotal);
	}
}
