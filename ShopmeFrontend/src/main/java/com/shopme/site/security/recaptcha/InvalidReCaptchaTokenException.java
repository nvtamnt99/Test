package com.shopme.site.security.recaptcha;

public class InvalidReCaptchaTokenException extends Exception {

	public InvalidReCaptchaTokenException(String message) {
		super(message);
	}

}
