package com.shopme.site.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.shopme.site.security.recaptcha.InvalidReCaptchaTokenException;
import com.shopme.site.security.recaptcha.ReCaptchaV3Handler;

public class CustomLoginFilter extends UsernamePasswordAuthenticationFilter {

	private ReCaptchaV3Handler recaptchaHandler;
	
	public CustomLoginFilter(String loginURL, String httpMethod) {
		setUsernameParameter("email");
		super.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(loginURL, httpMethod));
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		System.out.println("Before attempt Authentication...: " + getUsernameParameter());
		String recaptchaFormResponse = request.getParameter("g-recaptcha-response");
		
		try {
			float score = recaptchaHandler.verify(recaptchaFormResponse);
			if (score < 0.5) {
				request.setAttribute("email", request.getParameter("email"));
				request.getRequestDispatcher("email_otp").forward(request, response);
			}
			
		} catch (InvalidReCaptchaTokenException | IOException | ServletException e) {
			try {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		return super.attemptAuthentication(request, response);
	}

	public void setRecaptchaHandler(ReCaptchaV3Handler recaptchaHandler) {
		this.recaptchaHandler = recaptchaHandler;
	}

	 
}
		