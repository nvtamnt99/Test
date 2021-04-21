package com.shopme.site;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Question;
import com.shopme.common.entity.QuestionVote;
import com.shopme.site.question.vote.QuestionVoteRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class QuestionVoteRepositoryTests {
	@Autowired
	private QuestionVoteRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testVoteOneQuestion() {
		Integer questionId = 1;
		Question question = entityManager.find(Question.class, questionId);
		
		Integer customerId = 20;
		Customer customer = entityManager.find(Customer.class, customerId);
		
		QuestionVote vote = new QuestionVote();
		vote.setCustomer(customer);
		vote.setQuestion(question);
		vote.setVotes(1);
		
		QuestionVote questionVote = repo.save(vote);
		
		assertTrue(questionVote.getId() > 0);
	}
	
	@Test
	public void testFindByQuestionAndCustomer() {
		Integer questionId = 1;
		Integer customerId = 20;
		QuestionVote vote = repo.findByQuestionAndCustomer(questionId, customerId);
		
		assertTrue(vote.getId() > 0);
		
	}
}
