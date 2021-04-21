package com.shopme.admin.customer;


import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopme.admin.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@Service
@Transactional
public class CustomerServices {

	public static final int CUSTOMERS_PER_PAGE = 10;
	
	@Autowired 
	private CountryRepository countryRepo;
	
	@Autowired
	private CustomerRepository customerRepo;

	public List<Customer> listAll() {
		return customerRepo.findAll(Sort.by("firstName").ascending());
	}
	
	public Page<Customer> listAll(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, CUSTOMERS_PER_PAGE, sort);
		
		if (keyword != null) {
			return customerRepo.findAll(keyword, pageable);
		}
		
		return customerRepo.findAll(pageable);
	}
	
	public void updateCustomerEnabledStatus(Integer id, boolean enabled) {
		customerRepo.updateEnabledStatus(id, enabled);
	}
	
	public Customer get(Integer id) throws CustomerNotFoundException {
		try {
			return customerRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new CustomerNotFoundException("Could not find any customers with ID " + id);
		}
	}
	
	public void delete(Integer id) throws CustomerNotFoundException {
		Long count = customerRepo.countById(id);
		if (count == null || count == 0) {
			throw new CustomerNotFoundException("Could not find any customers with ID " + id);
		}
		
		customerRepo.deleteById(id);
	}
	
	public List<Country> listAllCountries() {
		return countryRepo.findAllByOrderByNameAsc();
	}	
	
	public void save(Customer customer) {
		customerRepo.save(customer);
	}
	
	public boolean isEmailUnique(Integer id, String email) {
		Customer existCustomer = customerRepo.findByEmail(email);

		if (existCustomer != null && existCustomer.getId() != id) {
			return false;
		}
		
		return true;
	}	
}
