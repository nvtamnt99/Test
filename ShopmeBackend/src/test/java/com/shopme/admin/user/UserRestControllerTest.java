package com.shopme.admin.user;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.shopme.admin.user.UserRepository;
import com.shopme.admin.user.UserRestController;
import com.shopme.admin.user.UserServices;

@WebMvcTest(UserRestController.class)
public class UserRestControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private UserServices service;
	
	@MockBean
	private UserRepository repo;
	
	@Test
	public void testCheckDuplicateEmailResponse() throws Exception {
		Integer id = 1;
		String email = "blahblah@gmail.com";
		when(service.isUniqueEmailViolated(id, email)).thenReturn(false);
		
		mvc.perform(post("/users/check_email")
				.param("id", "1")
				.param("email", email)
				.with(csrf())
			)
			.andExpect(status().isOk())
			.andExpect(content().string("OK"));
	}
}
