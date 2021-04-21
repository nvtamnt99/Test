package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.user.UserRepository;
import com.shopme.common.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UsersTest {

	@Autowired
	private UserRepository repo;
	
	@Test
	public void testEnableUser() {
		Integer userId = 5;
		
		repo.updateEnabledStatus(userId, true);
	}
	
	@Test
	public void testDisableUser() {
		Integer userId = 5;
		
		repo.updateEnabledStatus(userId, false);
	}	
	
	@Test
	public void testListUsersByRole() {
		List<User> users = repo.findByRoles_Name("Admin");
		users.forEach(user -> { System.out.println(user.getFullName());});
		
		assertThat(users.size()).isGreaterThan(0);
	}
}
