package com.shopme.admin.customer;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopme.common.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {

}
