package com.shopme.site.question.vote;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Question;
import com.shopme.common.entity.QuestionVote;
import com.shopme.site.question.QuestionRepository;

@Service
@Transactional
public class QuestionVoteServices {

	@Autowired
	private QuestionVoteRepository voteRepo;
	
	@Autowired
	private QuestionRepository questionRepo;
		
	public void voteUp(Integer questionId, Customer customer) throws QuestionVoteException {
		QuestionVote vote = voteRepo.findByQuestionAndCustomer(questionId, customer.getId());
		int score = 0;
		
		if (vote != null) {
			if (vote.isUpvoted()) {
				score = -1;
				undoVote(vote, questionId, score);
				throw new QuestionVoteException("You have unvoted up that question.");
			} else {
				score = 2;
			}
		} else {
			score = 1;
			Question question = questionRepo.findById(questionId).get();
			vote = new QuestionVote();
			vote.setQuestion(question);
			vote.setCustomer(customer);
		}
		
		vote.upVote();
		
		voteRepo.save(vote);
		questionRepo.vote(questionId, score);
	}
	
	public void voteDown(Integer questionId, Customer customer) throws QuestionVoteException {
		QuestionVote vote = voteRepo.findByQuestionAndCustomer(questionId, customer.getId());
		int score = 0;
		
		if (vote != null) {
			if (vote.isDownvoted()) {
				score = +1;
				undoVote(vote, questionId, score);
				throw new QuestionVoteException("You have unvoted down that question.");
			} else {
				score = -2;
			}
		} else {
			score = -1;
			Question question = questionRepo.findById(questionId).get();
			vote = new QuestionVote();
			vote.setQuestion(question);
			vote.setCustomer(customer);
		}
		
		vote.downVote();
		
		voteRepo.save(vote);
		questionRepo.vote(questionId, score);
	}
	
	public void undoVote(QuestionVote vote, Integer questionId, int score) {
		voteRepo.delete(vote);
		questionRepo.vote(questionId, score);		
	}
	
	public void markQuestionsVotedForProductByCustomer(List<Question> listQuestions, Integer productId, Integer customerId) {
		List<QuestionVote> listVotes = voteRepo.findByProductAndCustomer(productId, customerId);
		
		for (QuestionVote aVote : listVotes) {
			Question votedQuestion = aVote.getQuestion();
			if (listQuestions.contains(votedQuestion)) {
				int index = listQuestions.indexOf(votedQuestion);
				Question question = listQuestions.get(index);
				
				if (aVote.isUpvoted()) {
					question.setUpvotedByCurrentCustomer(true);
				} else if (aVote.isDownvoted()) {
					question.setDownvotedByCurrentCustomer(true);
				}
			}
		}
	}
	
}
