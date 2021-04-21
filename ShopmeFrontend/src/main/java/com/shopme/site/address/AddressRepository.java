package com.shopme.site.address;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Address;
import com.shopme.common.entity.Customer;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

	public List<Address> findByCustomer(Customer customer);
	
	@Query("SELECT a FROM Address a WHERE a.customer.id = ?1 AND a.defaultSelection = true")
	public Address findDefaultByCustomer(Integer customerId);
	
	@Query("UPDATE Address a SET a.defaultSelection = false WHERE a.id != ?1 AND a.customer.id = ?2")
	@Modifying
	public void setNonDefaultToOthers(Integer defaultAddressId, Integer customerId);
	
	@Modifying
	@Query("UPDATE Address a SET a.defaultSelection = true WHERE a.id = ?1")
	public void setDefaultAddress(Integer defaultAddressId);
	
	@Query("SELECT a FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
	public Address findByIdAndCustomer(Integer addressId, Integer customerId);
	
	@Query("DELETE FROM Address a WHERE a.id = ?1 AND a.customer.id = ?2")
	@Modifying
	public void deleteByIdAndCustomer(Integer addressId, Integer customerId);
}
