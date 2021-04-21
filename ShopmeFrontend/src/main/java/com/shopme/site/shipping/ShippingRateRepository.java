package com.shopme.site.shipping;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;

@Repository
public interface ShippingRateRepository extends JpaRepository<ShippingRate, Integer> {
	public ShippingRate findByCountryAndState(Country country, String state);
}
