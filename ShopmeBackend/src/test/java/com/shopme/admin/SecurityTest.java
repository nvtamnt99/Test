package com.shopme.admin;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
public class SecurityTest {
	@Autowired
	private WebApplicationContext context;
	private MockMvc mvc;
	
	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
	}
	
	@Test
	public void testFormLoginSuccess() throws Exception {
		mvc.perform(
				formLogin("/login").user("email", "namhm@codejava.net").password("codejava")
				).andExpect(status().isFound())
		.andExpect(redirectedUrl("/"));
	}
	
	@Test
	public void testFormLoginFailed() throws Exception {
		mvc.perform(
				formLogin("/login").user("email", "namhm@codejava.net").password("1234")
				).andExpect(status().isFound())
		.andExpect(redirectedUrl("/login?error"))
		.andDo(print());
	}	
}
