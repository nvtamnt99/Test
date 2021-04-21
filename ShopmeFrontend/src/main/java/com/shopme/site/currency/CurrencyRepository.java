package com.shopme.site.currency;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopme.common.entity.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
	public List<Currency> findAllByOrderByNameAsc();
}
