package com.shopme.admin;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String plaintext = "nam2020";
		String password = encoder.encode(plaintext);
		
		System.out.println(password);
	}

}
