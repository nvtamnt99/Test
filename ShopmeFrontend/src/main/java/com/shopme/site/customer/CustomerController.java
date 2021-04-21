package com.shopme.site.customer;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.site.Utility;
import com.shopme.site.security.CustomerUserDetails;
import com.shopme.site.security.oauth.CustomOAuth2User;
import com.shopme.site.setting.EmailSettingBag;
import com.shopme.site.setting.SettingServices;

@Controller
public class CustomerController {

	@Autowired
	private CustomerServices customerService;
	
	@Autowired
	private SettingServices settingService;
	
	
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		model.addAttribute("customer", new Customer());
		model.addAttribute("pageTitle", "Customer Registration");
		
		List<Country> countries = customerService.listAllCountries();
		model.addAttribute("listCountries", countries);
		
		return "register/register_form";
	}
	
	@PostMapping("/create_customer")
	public String createCustomer(Customer customer, HttpServletRequest request, Model model) 
			throws MessagingException, UnsupportedEncodingException {
		
		customerService.registerCustomer(customer);
		
		sendVerificationEmail(request, customer);
		
		model.addAttribute("pageTitle", "Registration Succeeded!");
		
		return "register/register_success";
	}

	
	private void sendVerificationEmail(HttpServletRequest request, Customer customer) throws MessagingException, UnsupportedEncodingException {
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		
		String toAddress = customer.getEmail();
		
		String subject = emailSettings.getCustomerVerifySubject();
		String content = emailSettings.getCustomerVerifyContent();
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		
		content = content.replace("[[name]]", customer.getFullName());
		String verifyURL = Utility.getSiteURL(request) + "/verify?code=" + customer.getVerificationCode();
		
		content = content.replace("[[URL]]", verifyURL);
		
		helper.setText(content, true);
		
		mailSender.send(message);
		
		System.out.println("Email has been sent");
	}
	
	@GetMapping("/verify")
	public String verifyAccount(@Param("code") String code, Model model) {
		System.out.println("Verificaton code: " + code);
		boolean verified = customerService.verify(code);
		
		String pageTitle = verified ? "Verification Succeeded!" : "Verification Failed";
		model.addAttribute("pageTitle", pageTitle);
		
		return "register/" + (verified ? "verify_success" : "verify_failed");
	}
	
	@GetMapping("/customer")
	public String viewCustomerHome(Model model, HttpServletRequest request) {
		Object principal = request.getUserPrincipal();
		String customerEmail = "";
		
		if (principal instanceof UsernamePasswordAuthenticationToken
				|| principal instanceof RememberMeAuthenticationToken
				|| principal instanceof PersistentRememberMeToken) {
			customerEmail = request.getUserPrincipal().getName();		
		} else if (principal instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
			CustomOAuth2User oauth2User = (CustomOAuth2User) token.getPrincipal();
			customerEmail = oauth2User.getEmail();
		}		
		
		Customer customer = customerService.getCustomerByEmail(customerEmail);
		
		List<Country> countries = customerService.listAllCountries();
		
		model.addAttribute("listCountries", countries);		
		model.addAttribute("pageTitle", customer.getFullName() + " - Customer Home");
		model.addAttribute("customer", customer);
		
		return "customer/customer_home";
	}
	
	@PostMapping("/customer/update_details")
	public String updateCustomerDetails(Customer customer, RedirectAttributes ra,
			HttpServletRequest request,
			@AuthenticationPrincipal CustomerUserDetails userDetails) {
		String redirectOption = request.getParameter("redirect");
		
		customerService.updateCustomer(customer);
		ra.addFlashAttribute("message", "Your account details have been updated");
		
		if (userDetails != null) {
			userDetails.setFirstName(customer.getFirstName());
			userDetails.setLastName(customer.getLastName());
		}

		if ("checkout".equals(redirectOption)) {
			return "redirect:/checkout";
		} else if ("address".equals(redirectOption)) {
			return "redirect:/address";
		}
		return "redirect:/customer";
	}
}
