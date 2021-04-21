package com.shopme.admin.customer;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;

@Controller
public class CustomerController {

	@Autowired
	private CustomerServices service;
	
	@GetMapping("/customers")
	public String listAll(Model model) {
		return listByPage(model, 1, "firstName", "asc", null);
	}

	@GetMapping("/customers/page/{pageNum}")
	public String listByPage(Model model, 
						@PathVariable(name = "pageNum") int pageNum,
						@Param("sortField") String sortField,
						@Param("sortDir") String sortDir,
						@Param("keyword") String keyword
			) {
		
		Page<Customer> page = service.listAll(pageNum, sortField, sortDir, keyword);
		List<Customer> listUsers = page.getContent();
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("listCustomers", listUsers);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		long startCount = (pageNum - 1) * CustomerServices.CUSTOMERS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + CustomerServices.CUSTOMERS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		if (page.getTotalPages() > 1) {
			model.addAttribute("pageTitle", "Customers (page " + pageNum + ")");
		} else {
			model.addAttribute("pageTitle", "Customers");
		}
		
		return "customers/customers";
	}
	
	@GetMapping("/customers/{id}/enabled/{status}")
	public RedirectView updateCustomerEnabledStatus(@PathVariable("id") Integer id,
			@PathVariable("status") boolean enabled, RedirectAttributes ra) {
		service.updateCustomerEnabledStatus(id, enabled);
		RedirectView rv = new RedirectView("/customers", true);
		String status = enabled ? "enabled" : "disabled";
		ra.addFlashAttribute("message", String.format("The customer ID %d has been %s.", id, status));
		
		return rv;
	}
	
	@GetMapping("/customers/detail/{id}")
	public String viewCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			Customer customer = service.get(id);
			model.addAttribute("customer", customer);
			
			return "customers/customer_detail_modal";
			
		} catch (CustomerNotFoundException ex) {
			ra.addFlashAttribute("message", "Could not find any customers with ID " + id);
			return "redirect:/customers";			
		}
	}
	
	@GetMapping("/customers/edit/{id}")
	public String editCustomer(@PathVariable("id") Integer id, Model model, RedirectAttributes ra) {
		try {
			Customer customer = service.get(id);
			List<Country> countries = service.listAllCountries();
			model.addAttribute("listCountries", countries);
			
			model.addAttribute("customer", customer);
			model.addAttribute("pageTitle", String.format("Edit Customer (ID: %d)", id));
			
			return "customers/customer_form";
			
		} catch (CustomerNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return "redirect:/customers";
		}
	}
	
	@PostMapping("/customers/save")
	public String saveCustomer(Customer customer, Model model, RedirectAttributes ra) throws CustomerNotFoundException {
		if (!customer.getPassword().isEmpty()) {
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			String encodedPassword = passwordEncoder.encode(customer.getPassword());
			customer.setPassword(encodedPassword);			
		} else {
			Customer existingCustomer = service.get(customer.getId());
			customer.setPassword(existingCustomer.getPassword());
		}
		
		service.save(customer);
		ra.addFlashAttribute("message", "The customer ID " + customer.getId() + " has been updated successfully.");
		return "redirect:/customers";
	}
	
	
	@GetMapping("/customers/delete/{id}")
	public String deleteCustomer(@PathVariable Integer id, RedirectAttributes ra) {
		try {
			service.delete(id);			
			ra.addFlashAttribute("message", "The customer ID " + id + " has been deleted successfully.");
			
		} catch (CustomerNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		
		return "redirect:/customers";
	}
	
	@GetMapping("/customers/export")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		response.setContentType("text/csv");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=customers_" + currentDateTime + ".csv";
		response.setHeader(headerKey, headerValue);
		
		List<Customer> customers = service.listAll();
		
		ICsvBeanWriter csvWriter = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
		String[] csvHeader = {"Customer ID", "Full Name", "E-mail", "Phone Number", "Address Line 1", "Address Line 2", "City", "State", "Country", "Postal Code", "Enabled", "Created Time"};
		String[] nameMapping = {"id", "fullName", "email", "phoneNumber", "addressLine1", "addressLine2", "city", "state", "countryName", "postalCode", "enabled", "createdTime"};
		
		csvWriter.writeHeader(csvHeader);		

		for (Customer customer : customers) {
			csvWriter.write(customer, nameMapping);
		}
		
		csvWriter.close();
	}
}