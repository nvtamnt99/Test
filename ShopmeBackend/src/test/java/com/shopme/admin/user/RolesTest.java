package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.user.RoleRepository;
import com.shopme.common.entity.Role;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RolesTest {
	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private RoleRepository repository;
	
	@Test
	public void testCreateRole() {
		Role role = new Role("Chief");
		entityManager.persist(role);
	}
	
	@Test
	public void testCreateRole2() {
		Role role = new Role("CEO");
		
		repository.save(role);
		
		Role role2 = repository.findByName("CEO");
		
		assertThat(role2.getName()).isEqualTo("CEO");
	}
}
