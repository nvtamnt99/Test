package com.shopme.admin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {

	@Test
	public void testBCryptPasswordEncoder() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String rawPassword = "alex2020";
		String encodedPassword = encoder.encode(rawPassword);
		
		System.out.println("Encoded password: " + encodedPassword);
		
		boolean matched = encoder.matches(rawPassword, encodedPassword);
		
		assertTrue(matched);
	}
}
