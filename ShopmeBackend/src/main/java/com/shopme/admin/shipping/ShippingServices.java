package com.shopme.admin.shipping;

import java.util.List;
import java.util.NoSuchElementException;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.country.CountryRepository;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;

@Service
@Transactional
public class ShippingServices {
	public static final int RATES_PER_PAGE = 10;
	
	@Autowired
	private ShippingRateRepository shipRepo;
	
	@Autowired
	private CountryRepository countryRepo;
	
	public List<ShippingRate> listAll() {
		return shipRepo.findAll();
	}
	
	public Page<ShippingRate> listAll(int pageNum, String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, RATES_PER_PAGE, sort);
		
		if (keyword != null) {
			return shipRepo.findAll(keyword, pageable);
		}
		
		return shipRepo.findAll(pageable);
	}	
	
	public List<Country> getCountryList() {
		return countryRepo.findAllByOrderByNameAsc();
	}	

	public void save(ShippingRate rate) throws ShippingRateAlreadyExistsException {
		ShippingRate existRate = shipRepo.findByCountryAndState(
				rate.getCountry().getId(), rate.getState());
		
		if (rate.getId() == null && existRate != null || (rate.getId() != null && existRate != null && !existRate.equals(rate))) {
			throw new ShippingRateAlreadyExistsException("There's already a rate for the destination "
						+ rate.getCountry().getName() + ", " + rate.getState()); 					
		}
		shipRepo.save(rate);
	}
	
	public ShippingRate get(Integer id) throws ShippingRateNotFoundException {
		try {
			return shipRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
		}
	}
	
	public void delete(Integer id) throws ShippingRateNotFoundException {
		Long count = shipRepo.countById(id);
		if (count == null || count == 0) {
			throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
			
		}
		shipRepo.deleteById(id);
	}
	
	public void updateCodSupport(Integer id, boolean codSupported) throws ShippingRateNotFoundException {
		try {
			shipRepo.findById(id).get();
			shipRepo.updateCODSupported(id, codSupported);
		} catch (NoSuchElementException ex) {
			throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
		}		
	}	
}
