package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.shopme.admin.user.RoleRepository;
import com.shopme.common.entity.Role;

@DataJpaTest
@TestMethodOrder(OrderAnnotation.class)
@Transactional
public class RolesTestAuto {

	@Autowired
	private RoleRepository repo;
	
	@Test
	@Order(1)
	@Rollback(false)
	public void testCreate() {
		String roleName = "ADMIN";
		Role role = new Role(roleName);
		
		repo.save(role);
		
		Role roleByName = repo.findByName(roleName);
		
		assertThat(roleByName.getName()).isEqualTo(roleName);
	}
	
	@Test
	@Order(2)
	public void testUpdate() {
		String roleName = "User";
		Role role = new Role(roleName);
		role.setId(1);
		
		repo.save(role);
		
		Role roleByName = repo.findByName(roleName);
		
		assertThat(roleByName.getName()).isEqualTo(roleName);		
	}
	
	@Test
	@Order(3)
	public void testList() {
		List<Role> roles = (List<Role>) repo.findAll();
		assertThat(roles).size().isGreaterThan(0);
	}
	
	@Test
	@Order(4)
	@Rollback(false)
	public void testDelete() {
		Integer id = 1;
		
		boolean present1 = repo.findById(id).isPresent();
		
		repo.deleteById(id);
		
		boolean present2 = repo.findById(id).isPresent();
		
		assertTrue(present1);
		assertFalse(present2);
	}
}
