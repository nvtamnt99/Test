package com.shopme.site;

import java.io.IOException;
import java.io.InputStream;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.shopme.site.security.recaptcha.ReCaptchaResponse;

@Controller
public class ContactController {

	@Value("${captchav2.url}")
	private String recaptchaServerURL;
	
	@Value("${captchav2.secret}")
	private String recaptchaSecret;	 
	
	@Autowired
	private JavaMailSender mailSender;
	
	@GetMapping("/contact")
	public String showContactForm(Model model) {
		model.addAttribute("pageTitle", "Contact Us");
		
		return "contact_form";
	}
	
	@PostMapping("/contact") 
	public String submitContactPlainText(HttpServletRequest request, Model model) {
		String fullName = request.getParameter("fullname");
		String email = request.getParameter("email");
		String subject = request.getParameter("subject");
		String content = request.getParameter("content");
		
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("contact@shopme.com");
		message.setTo("hainatu@gmail.com");
		
		String emailSubject = fullName + " has sent a message";
		String emailMessage = "Sender Name: " + fullName + "\n\n";
		emailMessage += "Sender E-mail: " + email + "\n\n";
		emailMessage += "Subject: " + subject + "\n\n";
		emailMessage += "Content: \n" + content + "\n";
		
		message.setSubject(emailSubject);
		
		message.setText(emailMessage);
		
		mailSender.send(message);		
		
		model.addAttribute("title", "Contact Us");
		model.addAttribute("pageTitle", "Contact Us");
		model.addAttribute("message", "Thanks for contacting us. We'll get back to you shortly.");
		
		return "message";
	}
	
	@PostMapping("/contact_html") 
	public String submitContactHTML(HttpServletRequest request, Model model) throws MessagingException {
		String fullName = request.getParameter("fullname");
		String email = request.getParameter("email");
		String subject = request.getParameter("subject");
		String content = request.getParameter("content");
		
		MimeMessage message = mailSender.createMimeMessage();				
		MimeMessageHelper helper = new MimeMessageHelper(message);
		
		helper.setFrom("contact@shopme.com");
		helper.setTo("hainatu@gmail.com");
		
		String emailSubject = fullName + " has sent a message";
		
		String emailMessage = "<p><b>Sender Name: </b>" + fullName + "</p>";
		emailMessage += "<p><b>Sender E-mail: </b>" + email + "</p>";
		emailMessage += "<p><b>Subject: </b><i>" + subject + "</i></p>";
		emailMessage += "<p><b>Content:</b></p><p>" + content + "</p>";
		
		helper.setSubject(emailSubject);
		
		helper.setText(emailMessage, true);
		
		mailSender.send(message);		
		
		model.addAttribute("title", "Contact Us");
		model.addAttribute("pageTitle", "Contact Us");
		model.addAttribute("message", "Thanks for contacting us. We'll get back to you shortly.");
		
		return "message";
	}
	
	@PostMapping("/contact_inline") 
	public String submitContactHTMLInlineLogo(HttpServletRequest request, Model model) throws MessagingException {
		String fullName = request.getParameter("fullname");
		String email = request.getParameter("email");
		String subject = request.getParameter("subject");
		String content = request.getParameter("content");
		
		MimeMessage message = mailSender.createMimeMessage();				
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		
		String emailSubject = fullName + " has sent a message";
		
		String emailMessage = "<p><b>Sender Name: </b>" + fullName + "</p>";
		emailMessage += "<p><b>Sender E-mail: </b>" + email + "</p>";
		emailMessage += "<p><b>Subject: </b><i>" + subject + "</i></p>";
		emailMessage += "<p><b>Content:</b></p><p>" + content + "</p>";
		emailMessage += "<hr><img src='cid:logoImage' />";
		
		helper.setFrom("contact@shopme.com");
		helper.setTo("hainatu@gmail.com");
		helper.setSubject(emailSubject);		
		helper.setText(emailMessage, true);
		
		ClassPathResource resource = new ClassPathResource("/static/images/ShopmeLogo.png");
		helper.addInline("logoImage", resource);		
		
		mailSender.send(message);		
		
		model.addAttribute("title", "Contact Us");
		model.addAttribute("pageTitle", "Contact Us");
		model.addAttribute("message", "Thanks for contacting us. We'll get back to you shortly.");
		
		return "message";
	}
	
	@PostMapping("/contact_attach") 
	public String submitContactHTMLAttachment(HttpServletRequest request, 
			HttpServletResponse response, Model model, 
			@RequestParam("attachment") MultipartFile multipartFile) throws MessagingException, IOException {
		String recaptchaFormResponse = request.getParameter("g-recaptcha-response");
		String fullName = request.getParameter("fullname");
		String email = request.getParameter("email");
		String subject = request.getParameter("subject");
		String content = request.getParameter("content");
		
		if(!verifyReCAPTCHA(recaptchaFormResponse)) {
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bots are not allowed here.");
		}
		
		MimeMessage message = mailSender.createMimeMessage();				
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		
		String emailSubject = fullName + " has sent a message";
		
		String emailMessage = "<p><b>Sender Name: </b>" + fullName + "</p>";
		emailMessage += "<p><b>Sender E-mail: </b>" + email + "</p>";
		emailMessage += "<p><b>Subject: </b><i>" + subject + "</i></p>";
		emailMessage += "<p><b>Content:</b></p><p>" + content + "</p>";
		
		helper.setFrom("contact@shopme.com");
		helper.setTo("hainatu@gmail.com");
		helper.setSubject(emailSubject);		
		helper.setText(emailMessage, true);
		
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			
			InputStreamSource source = new InputStreamSource() {
				
				@Override
				public InputStream getInputStream() throws IOException {
					return multipartFile.getInputStream();
				}
			};
			
			helper.addAttachment(fileName, source);
		}
		
		mailSender.send(message);		
		
		model.addAttribute("title", "Contact Us");
		model.addAttribute("pageTitle", "Contact Us");
		model.addAttribute("message", "Thanks for contacting us. We'll get back to you shortly.");
		
		return "message";
	}
	
	private boolean verifyReCAPTCHA(String recaptchaFormResponse) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("secret", recaptchaSecret);
		map.add("response", recaptchaFormResponse);
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
		
//		ResponseEntity<String> response = restTemplate.postForEntity(recaptchaServerURL, request, String.class);
		ReCaptchaResponse response = restTemplate.postForObject(recaptchaServerURL, request, ReCaptchaResponse.class);
		
		System.out.println("success: " + response.isSuccess());
		System.out.println("hostname: " + response.getHostname());
		System.out.println("challenge: " + response.getChallenge_ts());
		System.out.println("error codes: ");

		if (response.getErrorCodes() !=  null) {
			for (String error : response.getErrorCodes()) {
				System.out.println("\t" + error);
			}
		}
	
		return response.isSuccess();
	}
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
	
	@Autowired
	private RestTemplate restTemplate;
}
