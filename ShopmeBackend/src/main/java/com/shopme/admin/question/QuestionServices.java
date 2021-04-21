package com.shopme.admin.question;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Question;
import com.shopme.common.entity.User;

@Service
@Transactional
public class QuestionServices {
	
	public static final int QUESTIONS_PER_PAGE = 10;
	
	@Autowired
	private QuestionRepository repo;
	
	public List<Question> listAll() {
		return repo.findAll(Sort.by("askTime").descending());
	}
	
	public Page<Question> listAll(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, QUESTIONS_PER_PAGE, sort);
		
		if (keyword != null) {
			return repo.findAll(keyword, pageable);
		}
		
		return repo.findAll(pageable);
	}	
	
	public Question get(Integer id) throws QuestionNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new QuestionNotFoundException("Could not find question with ID " + id);
		}
	}
	
	public void save(Question questionInForm, User user) throws QuestionNotFoundException {
		try {
			Question existQuestion = repo.findById(questionInForm.getId()).get();
			existQuestion.setQuestionContent(questionInForm.getQuestionContent());
			existQuestion.setAnswer(questionInForm.getAnswer());
			existQuestion.setApproved(questionInForm.isApproved());
			
			if (existQuestion.isAnswered()) {
				existQuestion.setAnswerTime(new Date());
				existQuestion.setAnswerer(user);
			}
			
			repo.save(existQuestion);
			
		} catch (NoSuchElementException ex) {
			throw new QuestionNotFoundException("Could not find any question with ID " + questionInForm.getId());
		}		
	}
	
	public void approve(Integer id) {
		repo.updateApprovalStatus(id, true);
	}
	
	public void disapprove(Integer id) {
		repo.updateApprovalStatus(id, false);
	}
	
	public void delete(Integer id) throws QuestionNotFoundException {
		Long count = repo.countById(id);
		if (count == null || count == 0) {
			throw new QuestionNotFoundException("Could not find any question with ID " + id);
		}
		repo.deleteById(id);
	}	
}
