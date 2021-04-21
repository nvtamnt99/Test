package com.shopme.admin.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {

	@Autowired
	private UserServices service;
	
	@PostMapping("/users/check_email")
	public String checkDuplicateEmail(@Param("id") Integer id, @Param("email") String email) {
		if (service.isUniqueEmailViolated(id, email)) {
			return "Duplicated";
		} else {
			return "OK";
		}
	}
}
