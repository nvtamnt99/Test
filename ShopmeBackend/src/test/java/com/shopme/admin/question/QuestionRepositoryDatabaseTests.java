package com.shopme.admin.question;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.Question;
import com.shopme.common.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class QuestionRepositoryDatabaseTests {

	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private QuestionRepository repo;
	
	@Test
	public void testCreateQuestion() {
		Integer productId = 2;
		Integer userId = 12;
		Integer customerId = 1;
		
		Product product = entityManager.find(Product.class, productId);
		User user = entityManager.find(User.class, userId);
		Customer customer = entityManager.find(Customer.class, customerId);
		
		Question question = new Question();
		question.setProduct(product);		
		question.setAsker(customer);
		question.setAnswerer(user);
		question.setQuestionContent("This is a question");
		question.setAnswer("This is an answer");
		question.setVotes(1);
		question.setAnswerTime(new Date());
		question.setAskTime(new Date());
		
		Question savedQuestion = repo.save(question);
		
		assertTrue(savedQuestion.getId() > 0);
	}
}
