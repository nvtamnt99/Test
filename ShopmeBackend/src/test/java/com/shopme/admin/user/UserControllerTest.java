package com.shopme.admin.user;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.awt.Desktop;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.shopme.admin.user.RoleRepository;
import com.shopme.admin.user.UserController;
import com.shopme.admin.user.UserRepository;
import com.shopme.admin.user.UserServices;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@WebMvcTest(UserController.class)
public class UserControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private UserServices service;
	
	@MockBean
	private UserRepository repo;
	
	@MockBean
	private RoleRepository roleRepository;
	
	@Test
	public void testExportToCSV() throws Exception {
		List<User> listUsers = new ArrayList<>();
		Set<Role> roles = new HashSet<>();
		roles.add(new Role("ADMIN"));
		
		listUsers.add(new User(1, "namhm@codejava.net", "Nam Ha Minh", true, roles));
		listUsers.add(new User(2, "micheal@gmail.com", "Micheal Sah", true, roles));
		
		when(service.listAll()).thenReturn(listUsers);
		
		MvcResult result = mvc.perform(get("/users/export"))
			.andExpect(status().isOk()).andReturn();

		String header = result.getResponse().getHeader("Content-Disposition");
		String fileName = header.split("=")[1];
		
		byte[] content = result.getResponse().getContentAsByteArray();
		Path path = Paths.get(fileName);
		Files.write(path, content);
	}
}
