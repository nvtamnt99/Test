package com.shopme.site.shoppingcart;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Order;
import com.shopme.common.entity.PaymentMethod;
import com.shopme.common.entity.ShippingRate;
import com.shopme.site.Utility;
import com.shopme.site.address.AddressServices;
import com.shopme.site.customer.CustomerServices;
import com.shopme.site.order.OrderServices;
import com.shopme.site.payment.PayPalApiException;
import com.shopme.site.payment.PayPalRestApiService;
import com.shopme.site.setting.CurrencySettingBag;
import com.shopme.site.setting.EmailSettingBag;
import com.shopme.site.setting.SettingServices;

@Controller
public class ShoppingCartController {

	@Autowired
	private CustomerServices customerService;
	
	@Autowired
	private ShoppingCartServices cartService;
	
	@Autowired
	private AddressServices addressService;
	
	@Autowired
	private OrderServices orderService;
	
	@Autowired
	private SettingServices settingService;
	
	@Autowired
	private PayPalRestApiService paypalService;
	
	@GetMapping("/customer/cart")
	public String showCart(Model model, @AuthenticationPrincipal Authentication authentication) {
		Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
		List<CartItem> cartItems = cartService.listCartItems(customer);
		ShippingRate shippingRate = null;
		
		Address defaultAddress = addressService.getDefaultAddressOf(customer);
		
		if (defaultAddress != null) {
			shippingRate = cartService.getShippingRateForAddress(defaultAddress);
		} else {		
			shippingRate = cartService.getShippingRateForCustomer(customer);
		}		
		
		System.out.println("Shipping available? " + (shippingRate != null));
		
		if (shippingRate != null) {
			System.out.println(shippingRate.getCountry().getName());
			System.out.println(shippingRate.getState());
			System.out.println(shippingRate.getRate());
			System.out.println(shippingRate.getDays());
			System.out.println("COD Supported?" + shippingRate.isCodSupported());
		}
		
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("shippingRate", shippingRate);
		model.addAttribute("pageTitle", "Shopping Cart");
		
		return "cart";
	}
	
	@GetMapping("/checkout")
	public String showCheckOutPage(Model model, @AuthenticationPrincipal Authentication authentication,
			HttpServletRequest request) {
		Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
		
		Address defaultAddress = addressService.getDefaultAddressOf(customer);
		ShippingRate shippingRate = null;
		
		if (defaultAddress != null) {
			model.addAttribute("shippingAddress", defaultAddress.toString());
			shippingRate = cartService.getShippingRateForAddress(defaultAddress);
		} else {		
			model.addAttribute("shippingAddress", customer.getAddress());
			shippingRate = cartService.getShippingRateForCustomer(customer);
		}
		
		if (shippingRate == null) {
			return "redirect:/cart";
		}
		
		String currencyCode = settingService.getCurrencyCode();
		
		List<CartItem> cartItems = cartService.listCartItems(customer);
		float productTotal = cartService.calculateProductTotal(cartItems);
		
		float shippingCost = cartService.calculateShippingCost(cartItems, shippingRate);
		float paymentTotal = productTotal + shippingCost;
		Date deliverDate = orderService.calculateDeliverDate(shippingRate.getDays());

		model.addAttribute("currencyCode", currencyCode);
		model.addAttribute("cartItems", cartItems);
		model.addAttribute("customer", customer);
		
		model.addAttribute("productTotal", productTotal);
		model.addAttribute("shippingCost", shippingCost);
		model.addAttribute("paymentTotal", paymentTotal);
		model.addAttribute("paymentTotal4PayPal", formatCurrencyForPayPal(paymentTotal));
		model.addAttribute("shippingRate", shippingRate);
		model.addAttribute("deliverDate", deliverDate);
		model.addAttribute("pageTitle", "Checkout");
		
		return "checkout";
	}

	@PostMapping("/process_paypal_order")
	public String processPayPalOrder(Model model, @AuthenticationPrincipal Authentication authentication, 
			HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		String orderId = request.getParameter("orderId");
		String totalAmount = request.getParameter("totalAmount");
		
		System.out.println("orderId: " + orderId);
		System.out.println("totalAmount: " + totalAmount);
		
		String pageTitle = null;
		String message = null;
		
		try {
			if (paypalService.validatePayPalOrder(orderId, totalAmount)) {
//				return "redirect:/";
				return placeOrder(model, authentication, request);
			} else {
				pageTitle = "Checkout Failure";
				message = "ERROR: Transaction could not be completed because order information is invalid.";
			}
		} catch (PayPalApiException e) {
			pageTitle = "Checkout Failure";
			message = "ERROR: Transaction failed due to error: " + e.getMessage();
			e.printStackTrace();
		}
		
		model.addAttribute("title", pageTitle);
		model.addAttribute("pageTitle", pageTitle);
		model.addAttribute("message", message);
		
		return "message";
	}
	
	@PostMapping("/place_order")
	public String placeOrder(Model model, @AuthenticationPrincipal Authentication authentication, 
			HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		String paymentType = request.getParameter("paymentMethod");
		System.out.println("Payment Method: " + paymentType);
		
		Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
		
		Address shippingAddress = addressService.getDefaultAddressOf(customer);
		List<CartItem> cartItems = cartService.listCartItems(customer);
		float productTotal = cartService.calculateProductTotal(cartItems);
		float productCost = cartService.calculateProductCost(cartItems);

		ShippingRate shippingRate = null;
		
		if (shippingAddress != null) {
			shippingRate = cartService.getShippingRateForAddress(shippingAddress);
		} else {		
			model.addAttribute("shippingAddress", customer.getAddress());
			shippingRate = cartService.getShippingRateForCustomer(customer);
		}
		
		float shippingCost = cartService.calculateShippingCost(cartItems, shippingRate);
		float paymentTotal = productTotal + shippingCost;
		
		PaymentMethod paymentMethod = PaymentMethod.valueOf(paymentType);
		
		
		Order savedOrder = orderService.placeOrder(customer, shippingAddress, cartItems, paymentMethod, 
							productTotal, productCost, shippingCost, paymentTotal, shippingRate.getDays());
		sendOrderConfirmationEmail(request, savedOrder);
		
		model.addAttribute("pageTitle", "Order Completed");
		
		return "order_completed";
	}
	
	private void sendOrderConfirmationEmail(HttpServletRequest request, Order order) throws UnsupportedEncodingException, MessagingException {
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		
		String toAddress = order.getCustomer().getEmail();
		
		String subject = emailSettings.getOrderConfirmationSubject();
		String content = emailSettings.getOrderConfirmationContent();
		
		subject = subject.replace("[[orderId]]", String.valueOf(order.getId()));
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		
		DateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss E, dd MMM yyyy");
		String orderTime = dateFormatter.format(order.getOrderTime());
		
		String orderTotal = formatCurrency(order.getTotal());
		
		content = content.replace("[[name]]", order.getCustomer().getFullName());
		content = content.replace("[[orderId]]", String.valueOf(order.getId()));
		content = content.replace("[[orderTime]]", orderTime);
		content = content.replace("[[shippingAddress]]", order.getShippingAddress());
		content = content.replace("[[total]]", orderTotal);
		content = content.replace("[[paymentMethod]]", order.getPaymentMethod().toString());
		
		String orderURL = Utility.getSiteURL(request) + "/orders";
		String orderLink = "<a href=\"" + orderURL + "\">Click here</a>";
		
		content = content.replace("[[orderLink]]", orderLink);
		
		helper.setText(content, true);
		
		mailSender.send(message);
		
		System.out.println("Order Confirmation Email has been sent");		
	}
	
	private String formatCurrencyForPayPal(float amount) {
		DecimalFormat formatter = new DecimalFormat("###,###.##");
		return formatter.format(amount);
	}
	
	private String formatCurrency(float amount) {
		CurrencySettingBag settings = settingService.getCurrencySettingBag();
		
		String symbol = settings.getSymbol();
		String symbolPosition = settings.getSymbolPosition();
		String decimalPointType = settings.getDecimalPointType();
		String thousandPointType = settings.getThousandPointType();
		String decimalDigits = settings.getDecimalDigits();
		
		String pattern = symbolPosition.equals("before") ? symbol : "";
		
		char thousandSeparator = thousandPointType.equals("POINT") ? '.' : ',';
		char decimalSeparator = decimalPointType.equals("POINT") ? '.' : ',';
		
		pattern += "###,###";
		
		
		int numberOfDigitsAfterDecimalPoint = Integer.parseInt(decimalDigits);
		
		if (numberOfDigitsAfterDecimalPoint > 0) {
			pattern += ".";
			for (int i = 1; i <= numberOfDigitsAfterDecimalPoint; i++) {
				pattern += "#";
			}
		}		
				
		pattern += symbolPosition.equals("after") ? symbol : "";
		
		System.out.println("Currency pattern: " + pattern);
		
		DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
		decimalFormatSymbols.setDecimalSeparator(decimalSeparator);
		decimalFormatSymbols.setGroupingSeparator(thousandSeparator);
		
		DecimalFormat formatter = new DecimalFormat(pattern, decimalFormatSymbols);
		
		return formatter.format(amount);
	}
}
