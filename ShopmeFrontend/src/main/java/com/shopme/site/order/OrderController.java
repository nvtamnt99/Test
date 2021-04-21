package com.shopme.site.order;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Order;
import com.shopme.site.customer.CustomerServices;

@Controller
public class OrderController {

	@Autowired
	private OrderServices orderService;
	
	@Autowired
	private CustomerServices customerService;	
	
	@GetMapping("/customer/orders")
	public String listOrders(Model model, HttpServletRequest request,
			@AuthenticationPrincipal Authentication authentication) {
		return listOrdersByPage(model, authentication, request, 1, "orderTime", "desc", null);
	}
	
	@GetMapping("/customer/orders/page/{pageNum}")
	public String listOrdersByPage(Model model,
						@AuthenticationPrincipal Authentication authentication,			
						HttpServletRequest request,
						@PathVariable(name = "pageNum") int pageNum,
						@Param("sortField") String sortField,
						@Param("sortDir") String sortDir,
						@Param("keyword") String keyword
			) {
		Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
		
		Page<Order> page = orderService.getOrdersForCustomer(
								customer, pageNum, sortField, sortDir, keyword);
		List<Order> listOrders = page.getContent();
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("listOrders", listOrders);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		long startCount = (pageNum - 1) * OrderServices.ORDERS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + OrderServices.ORDERS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		if (page.getTotalPages() > 1) {
			model.addAttribute("pageTitle", "My Orders (page " + pageNum + ")");
		} else {
			model.addAttribute("pageTitle", "My Orders");
		}
		
		return "orders/orders";		
	}
	
	@GetMapping("/customer/orders/detail/{id}")
	public String viewOrderDetails(Model model,
			@PathVariable(name = "id") Integer id,
			@AuthenticationPrincipal Authentication authentication) {
		Customer customer = customerService.getCurrentlyLoggedInCustomer(authentication);
		
		Order order = orderService.getOrderDetails(id, customer);
		
		model.addAttribute("pageTitle", "Order Details");
		model.addAttribute("order", order);
		
		return "orders/order_detail_modal";
	}	
}
