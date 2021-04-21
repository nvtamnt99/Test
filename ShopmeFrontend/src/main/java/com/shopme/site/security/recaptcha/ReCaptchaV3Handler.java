package com.shopme.site.security.recaptcha;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

//@Configuration("reCaptchaV3Handler")
public class ReCaptchaV3Handler {
	
	private String recaptchaServerURL = "https://www.google.com/recaptcha/api/siteverify";
	
	private String recaptchaSecret = "6LcYp8sZAAAAACIU1tFgo-BlkWzarCfviPtJi1ol";
	
	public float verify(String recaptchaFormResponse) throws InvalidReCaptchaTokenException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("secret", recaptchaSecret);
		map.add("response", recaptchaFormResponse);
		
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
		
//		ResponseEntity<String> response = restTemplate.postForEntity(recaptchaServerURL, request, String.class);
		ReCaptchaV3Response response = restTemplate.postForObject(recaptchaServerURL, request, ReCaptchaV3Response.class);
		
		System.out.println("success: " + response.isSuccess());
		System.out.println("score: " + response.getScore());
		System.out.println("hostname: " + response.getHostname());
		System.out.println("action: " + response.getAction());
		System.out.println("challenge: " + response.getChallenge_ts());
		System.out.println("error codes: ");

		if (response.getErrorCodes() !=  null) {
			for (String error : response.getErrorCodes()) {
				System.out.println("\t" + error);
			}
		}
		
		if (!response.isSuccess()) {
			throw new InvalidReCaptchaTokenException("Invalid reCAPTCHA Token");
		}
		
		return 0.4f;
		//return response.getScore();
	}
	
	@Autowired
	private RestTemplate restTemplate;	
}
