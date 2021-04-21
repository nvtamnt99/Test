package com.shopme.site.shoppingcart;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.ShippingRate;
import com.shopme.site.product.ProductRepository;
import com.shopme.site.shipping.ShippingRateRepository;

@Service
@Transactional
public class ShoppingCartServices {
	static final int DIM_DIVISOR = 139;

	@Autowired
	private CartItemRepository cartRepo;
	
	@Autowired
	private ProductRepository productRepo;
	
	@Autowired
	private ShippingRateRepository shipRepo;
	
	public List<CartItem> listCartItems(Customer customer) {
		return cartRepo.findByCustomer(customer);
	}
	
	public Integer addProduct(Integer productId, Integer quantity, Customer customer) {
		Integer addedQuantity = quantity;
		
		Product product = productRepo.findById(productId).get();
		CartItem cartItem = cartRepo.findByCustomerAndProduct(customer, product);
		
		if (cartItem != null) {
			addedQuantity = cartItem.getQuantity() + quantity;
			cartItem.setQuantity(addedQuantity);
		} else {
			cartItem = new CartItem();
			cartItem.setCustomer(customer);
			cartItem.setProduct(product);
			cartItem.setQuantity(addedQuantity);
		}
		
		cartRepo.save(cartItem);
		
		return addedQuantity;
	}
	
	public void removeProduct(Integer productId, Customer customer) {
		cartRepo.deleteByCustomerAndProduct(customer.getId(), productId);
	}
	
	public float updateQuantity(Integer productId, Integer quantity, Customer customer) {
		cartRepo.updateQuantity(quantity, productId, customer.getId());
		Product product = productRepo.findById(productId).get();
		float subtotal = product.getDiscountPrice() * quantity; 
		return subtotal;
	}
	
	public ShippingRate getShippingRateForCustomer(Customer customer) {
		String state = customer.getState();
		if (state == null || state.isEmpty()) {
			state = customer.getCity();
		}
		return shipRepo.findByCountryAndState(customer.getCountry(), state);
	}

	public ShippingRate getShippingRateForAddress(Address address) {
		String state = address.getState();
		if (state == null || state.isEmpty()) {
			state = address.getCity();
		}
		return shipRepo.findByCountryAndState(address.getCountry(), state);
	}

	public float calculateProductCost(List<CartItem> cartItems) {
		float cost = 0.0f;
		
		for (CartItem item : cartItems) {
			cost += item.getQuantity() * item.getProduct().getCost();
		}
		
		return cost;
	}
	
	public float calculateProductTotal(List<CartItem> cartItems) {
		float total = 0.0f;
		
		for (CartItem item : cartItems) {
			total += item.getSubtotal();
		}
		
		return total;
	}
	
	public float calculateShippingCost(List<CartItem> cartItems, ShippingRate rate) {
		float shippingCost = 0.0f;
		
		for (CartItem item : cartItems) {
			Product product = item.getProduct();
			float dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / DIM_DIVISOR;
			float finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;
			float cost = finalWeight * rate.getRate() * item.getQuantity();
			
			item.setShip(cost);
			
			shippingCost += cost;
		}		
		
		return shippingCost;
	}
}
