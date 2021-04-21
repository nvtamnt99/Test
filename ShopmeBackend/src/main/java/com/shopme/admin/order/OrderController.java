package com.shopme.admin.order;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.product.ProductServices;
import com.shopme.admin.setting.SettingServices;
import com.shopme.common.entity.Order;
import com.shopme.common.entity.OrderDetail;
import com.shopme.common.entity.OrderStatus;
import com.shopme.common.entity.OrderTrack;
import com.shopme.common.entity.Product;
import com.shopme.common.entity.Setting;

@Controller
public class OrderController {

	@Autowired
	private OrderServices orderService;
	
	@Autowired
	private ProductServices productService;
	
	@Autowired
	private SettingServices settingService;
	
	@GetMapping("/orders")
	public String listAll(Model model, HttpServletRequest request) {
		return listByPage(model, request, 1, "orderTime", "desc", null);
	}
	
	@GetMapping("/orders/page/{pageNum}")
	public String listByPage(Model model, HttpServletRequest request,
						@PathVariable(name = "pageNum") int pageNum,
						@Param("sortField") String sortField,
						@Param("sortDir") String sortDir,
						@Param("keyword") String keyword
			) {
		
		Page<Order> page = orderService.listAll(pageNum, sortField, sortDir, keyword);
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
			model.addAttribute("pageTitle", "Orders (page " + pageNum + ")");
		} else {
			model.addAttribute("pageTitle", "Orders");
		}
		
		loadCurrencySetting(request);
		
		return "orders/orders";
	}	
	
	private void loadCurrencySetting(HttpServletRequest request) {
		List<Setting> currencySettings = settingService.getCurrencySettings();
		
		for (Setting setting : currencySettings) {
			request.setAttribute(setting.getKey(), setting.getValue());
		}	
	}
	
	@GetMapping("/orders/detail/{id}")
	public String viewOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra,
			HttpServletRequest request) {
		try {
			Order order = orderService.get(id);
			loadCurrencySetting(request);
			model.addAttribute("order", order);
			
			return "orders/order_detail_modal";
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return "redirect:/orders";
		}
		
	}
	
	@GetMapping("/orders/delete/{id}")
	public String deleteOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra,
			HttpServletRequest request) {
		try {
			orderService.delete(id);;
			loadCurrencySetting(request);
			
			ra.addFlashAttribute("message", "The order ID " + id + " has been deleted.");
			
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
		}
		return "redirect:/orders";
		
	}
	
	@GetMapping("/orders/edit/{id}")
	public String editOrder(@PathVariable("id") Integer id, Model model, RedirectAttributes ra,
			HttpServletRequest request) {
		try {
			Order order = orderService.get(id);;
			
			model.addAttribute("pageTitle", "Edit Order (ID: " + id + ")");
			model.addAttribute("order", order);
			
			return "orders/order_form";
			
		} catch (OrderNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return "redirect:/orders";
		}
		
	}
	
	@GetMapping("/orders/search_product")
	public String showSearchProductPage(Model model,
			HttpServletResponse response) {
		return "orders/search_product";
	}
	
	@PostMapping("/order/save")
	public String saveOrder(Order order, HttpServletRequest request, RedirectAttributes ra)
			throws ParseException {
		printOrderDetailsForDebugging(order, request);		
		
		updateOrderDetails(order, request);
		updateTrackDetails(order, request);
		
		orderService.save(order);
		
		ra.addFlashAttribute("message", "The order ID "
				+ order.getId() + " has been updated successfully.");
		
		return "redirect:/orders";
	}

	private void printOrderDetailsForDebugging(Order order, HttpServletRequest request) {
		String deliverDateOnForm = order.getDeliverDateOnForm();
		System.out.println("Order ID: " + order.getId());
		System.out.println("Subtotal: " + order.getSubtotal());
		System.out.println("Shipping Cost: " + order.getShippingCost());
		System.out.println("Tax: " + order.getTax());
		System.out.println("Total: " + order.getTotal());		
		System.out.println("deliverDateOnForm: " + deliverDateOnForm);
		
		String[] detailIds = request.getParameterValues("detailId");
		String[] productIds = request.getParameterValues("productId");
		String[] quantities = request.getParameterValues("quantity");
		String[] prices = request.getParameterValues("price");
		String[] costs = request.getParameterValues("productCost");
		String[] ships = request.getParameterValues("productShip");		
		String[] subtotals = request.getParameterValues("productSubtotal");
		
		System.out.println("-- Detail IDs --");
		for (String did : detailIds) {
			System.out.print(did + " - ");
		}
		
		System.out.println("\n-- Product IDs --");
		for (String pid : productIds) {
			System.out.print(pid + " - ");
		}
		
		System.out.println("\n-- Product Quantities --");
		for (String pqty : quantities) {
			System.out.print(pqty + " - ");
		}		
		
		System.out.println("\n-- Product Prices --");
		for (String pprice : prices) {
			System.out.print(pprice + " - ");
		}
		
		System.out.println("\n-- Product Costs --");
		for (String pcost : costs) {
			System.out.print(pcost + " - ");
		}

		System.out.println("\n-- Product Ships --");
		for (String pship : ships) {
			System.out.print(pship + " - ");
		}
		
		System.out.println("\n-- Product Subtotals --");
		for (String psubtotal : subtotals) {
			System.out.print(psubtotal + " - ");
		}		
		
		String[] trackIds = request.getParameterValues("trackId");
		String[] trackDates = request.getParameterValues("trackDate");
		String[] trackStatuses = request.getParameterValues("trackStatus");
		String[] trackNotes = request.getParameterValues("trackNotes");
		
		System.out.println("\n-- Track IDs --");
		for (String tid : trackIds) {
			System.out.print(tid + " - ");
		}
		
		System.out.println("\n-- Track Dates --");
		for (String tdate : trackDates) {
			System.out.print(tdate + " - ");
		}
		
		System.out.println("\n-- Track Statuses --");
		for (String tstatus : trackStatuses) {
			System.out.print(tstatus + " - ");
		}		
		
		System.out.println("\n-- Track Notes --");
		for (String tnote : trackNotes) {
			System.out.print(tnote + " - ");
		}
	}
	
	private void updateOrderDetails(Order order, HttpServletRequest request) {
		Set<OrderDetail> orderDetails = order.getOrderDetails();
		
		String[] detailIds = request.getParameterValues("detailId");
		String[] productIds = request.getParameterValues("productId");
		String[] quantities = request.getParameterValues("quantity");
		String[] prices = request.getParameterValues("price");
		String[] costs = request.getParameterValues("productCost");
		String[] ships = request.getParameterValues("productShip");
		String[] subtotals = request.getParameterValues("productSubtotal");
		
		for (int i = 0; i < productIds.length; i++) {
			OrderDetail aDetail = new OrderDetail();

			int orderDetailId = Integer.parseInt(detailIds[i]);
			if (orderDetailId > 0) {
				aDetail.setId(orderDetailId);
			}
			
			aDetail.setOrder(order);
			aDetail.setProduct(new Product(Integer.parseInt(productIds[i])));
			aDetail.setCost(Float.parseFloat(costs[i]));
			aDetail.setShip(Float.parseFloat(ships[i]));
			aDetail.setQuantity(Integer.parseInt(quantities[i]));
			aDetail.setUnitPrice(Float.parseFloat(prices[i]));
			aDetail.setSubtotal(Float.parseFloat(subtotals[i]));
			
			orderDetails.add(aDetail);
		}		
	}
	
	private void updateTrackDetails(Order order, HttpServletRequest request) throws ParseException {
		List<OrderTrack> orderTracks = order.getOrderTracks();
		
		String[] trackIds = request.getParameterValues("trackId");
		String[] trackDates = request.getParameterValues("trackDate");
		String[] trackStatuses = request.getParameterValues("trackStatus");
		String[] trackNotes = request.getParameterValues("trackNotes");
		
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		
		for (int i = 0; i < trackIds.length; i++) {
			OrderTrack aTrack = new OrderTrack();
			int trackId = Integer.parseInt(trackIds[i]);
			if (trackId > 0) {
				aTrack.setId(trackId);
			}
			aTrack.setOrder(order);
			aTrack.setUpdatedTime(dateFormatter.parse(trackDates[i]));
			aTrack.setStatus(OrderStatus.valueOf(trackStatuses[i]));
			aTrack.setNotes(trackNotes[i]);
			
			orderTracks.add(aTrack);
		}
	}

	@PostMapping("/orders/search_product")
	public String searchProducts(Model model, @Param("keyword") String keyword) {
		return searchProductsByPage(model, 1, keyword);
	}
	
	@GetMapping("/orders/search_product/page/{pageNum}")
	public String searchProductsByPage(Model model,
			@PathVariable(name = "pageNum") int pageNum,
			@Param("keyword") String keyword) {

		Page<Product> page = productService.searchProducts(pageNum, keyword);
		List<Product> listProducts = page.getContent();
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("listProducts", listProducts);
		model.addAttribute("keyword", keyword);
		
		long startCount = (pageNum - 1) * ProductServices.PRODUCTS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + ProductServices.PRODUCTS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		return "orders/search_product";
	}
}
