package com.shopme.admin.user;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.User;

@Service
@Transactional
public class UserServices {
	public static final int USERS_PER_PAGE = 4;
	
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public List<User> listAll() {
		return repo.findAll(Sort.by("firstName").ascending());
	}
	
	public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);
		
		if (keyword != null) {
			return repo.findAll(keyword, pageable);
		}
		
		return repo.findAll(pageable);
	}
	
	public User save(User user) {
		boolean updateExistingUser = user.getId() != null;
		if (updateExistingUser) {
			User existingUser = repo.findById(user.getId()).get();
			
			if (user.getPassword().isEmpty()) {
				user.setPassword(existingUser.getPassword());
			} else {
				encodePassword(user);
			}
		} else {
			encodePassword(user);
		}
		
		return repo.save(user);
	}
	
	public User updateAccount(User user) {
		User existingUser = repo.findById(user.getId()).get();
		
		if (!user.getPassword().isEmpty()) {
			existingUser.setPassword(user.getPassword());
			encodePassword(existingUser);
		}
		
		if (user.getPhotos() != null) {
			existingUser.setPhotos(user.getPhotos());
		}
		
		existingUser.setFirstName(user.getFirstName());
		existingUser.setLastName(user.getLastName());
		
		return repo.save(existingUser);
	}
	
	private void encodePassword(User user) {
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);		
	}
	
	public User get(Integer id) throws UserNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}
	}
	
	public User getByEmail(String email) {
		return repo.getUserByEmail(email);
	}
	
	public void delete(Integer id) throws UserNotFoundException {
		Long count = repo.countById(id);
		if (count == null || count == 0) {
			throw new UserNotFoundException("Could not find any user with ID " + id);
		}
		
		repo.deleteById(id);
	}
	
	public void updateUserEnabledStatus(Integer id, boolean enabled) {
		repo.updateEnabledStatus(id, enabled);
	}
		
	public boolean isUniqueEmailViolated(Integer id, String email) {
		boolean isUniqueEmailViolated = false;
		
		User userByEmail = repo.getUserByEmail(email);
		
		if (userByEmail == null) return false;
		
		boolean isCreatingNew = (id == null || id == 0);
		
		if (isCreatingNew) {
			if (userByEmail != null) isUniqueEmailViolated = true; 
		} else {
			if (userByEmail.getId() != id) {
				isUniqueEmailViolated = true;
			}
		}		
		
		return isUniqueEmailViolated;
	}
	
}
