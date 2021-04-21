package com.shopme.admin.question;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.EntityFactory;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.Question;
import com.shopme.common.entity.User;

@DataJpaTest
@TestPropertySource(locations = "classpath:test.properties")
public class QuestionRepositoryInMemoryTests {

	@Autowired
	private QuestionRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@BeforeEach
	public void setupData() {
		User user1 = entityManager.persist(EntityFactory.randomUser());
		Customer customer1 = entityManager.persist(EntityFactory.randomCustomer());
		Product product1 = entityManager.persist(EntityFactory.randomProduct());
		
	}
	
	@Test
	public void testCreateQuestion() {
		Question question = new Question();
		question.setQuestionContent("This is a question");
		
		// implement in-memory test later
		
	}
}
