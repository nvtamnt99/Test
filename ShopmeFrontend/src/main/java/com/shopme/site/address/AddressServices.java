package com.shopme.site.address;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;

@Service
@Transactional
public class AddressServices {
	@Autowired
	private AddressRepository repo;
	
	public List<Address> listAddressesOf(Customer customer) {
		return repo.findByCustomer(customer);
	}
	
	public void save(Address address, Customer customer) {
		Address savedAddress = repo.save(address);
		
		if (savedAddress.isDefaultSelection()) {
			repo.setNonDefaultToOthers(savedAddress.getId(), customer.getId());
		}		
	}
	
	public Address getDefaultAddressOf(Customer customer) {
		return repo.findDefaultByCustomer(customer.getId());
	}
	
	public void setDefaultAddress(Integer defaultAddressId, Customer customer) {
		if (defaultAddressId == 0) {
			// use customer's address - set non-default to all other addresses
			repo.setNonDefaultToOthers(0, customer.getId());
		} else if (defaultAddressId > 0) {
			repo.setDefaultAddress(defaultAddressId);
			repo.setNonDefaultToOthers(defaultAddressId, customer.getId());
		}
	}
	
	public Address get(Integer id, Customer customer) {
		return repo.findByIdAndCustomer(id, customer.getId());
	}
	
	public void delete(Integer id, Customer customer) {
		repo.deleteByIdAndCustomer(id, customer.getId());
	}
}
