package com.shopme.admin.shipping;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;

@Controller
public class ShippingController {
	
	@Autowired
	private ShippingServices service;
	
	@GetMapping("/shipping")
	public String listAll(Model model) {
		return listByPage(model, 1, "country", "asc", null);
	}
	
	@GetMapping("/shipping/page/{pageNum}")
	public String listByPage(Model model, 
						@PathVariable(name = "pageNum") int pageNum,
						@Param("sortField") String sortField,
						@Param("sortDir") String sortDir,
						@Param("keyword") String keyword
			) {
		
		Page<ShippingRate> page = service.listAll(pageNum, sortField, sortDir, keyword);
		List<ShippingRate> shippingRates = page.getContent();
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("shippingRates", shippingRates);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		long startCount = (pageNum - 1) * ShippingServices.RATES_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + ShippingServices.RATES_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		if (page.getTotalPages() > 1) {
			model.addAttribute("pageTitle", "Shipping Rates (page " + pageNum + ")");
		} else {
			model.addAttribute("pageTitle", "Shipping Rates");
		}
		
		return "shipping/shipping_rates";
	}	
	
	@GetMapping("/shipping/new")
	public String newRate(Model model) {
		List<Country> listCountries = service.getCountryList();
		
		model.addAttribute("rate", new ShippingRate());
		model.addAttribute("pageTitle", "New Shipping Rate");
		model.addAttribute("listCountries", listCountries);
		
		return "shipping/rate_form";
	}

	@PostMapping("/shipping/save")
	public String saveRate(ShippingRate rate, RedirectAttributes ra) {
		try {
			service.save(rate);
			ra.addFlashAttribute("message", "The shipping rate has been saved successfully.");
		} catch (ShippingRateAlreadyExistsException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/shipping";
	}
	
	@GetMapping("/shipping/edit/{id}")
	public String editRate(@PathVariable(name = "id") Integer id,
			Model model, RedirectAttributes ra) {
		try {
			ShippingRate rate = service.get(id);
			List<Country> listCountries = service.getCountryList();
			
			model.addAttribute("listCountries", listCountries);			
			model.addAttribute("rate", rate);
			model.addAttribute("pageTitle", "Edit Shipping Rate (ID: " + id + ")");
			
			return "shipping/rate_form";
		} catch (ShippingRateNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return "redirect:/shipping";
		}
	}
	
	@GetMapping("/shipping/delete/{id}")
	public String deleteRate(@PathVariable(name = "id") Integer id,
			Model model, RedirectAttributes ra) {
		try {
			service.delete(id);
			ra.addFlashAttribute("message", "The shipping rate ID " + id + " has been deleted.");
		} catch (ShippingRateNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/shipping";
	}
	
	@GetMapping("/shipping/cod/{id}/{supported}")
	public String updateCODSupport(@PathVariable(name = "id") Integer id,
			@PathVariable(name = "supported") Boolean supported,
			Model model, RedirectAttributes ra) {
		try {
			service.updateCodSupport(id, supported);
			ra.addFlashAttribute("message", "COD suport for shipping rate ID " + id + " has been updated.");
		} catch (ShippingRateNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/shipping";
	}	
}
