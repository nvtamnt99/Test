package com.shopme.admin.report;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.order.OrderRepository;
import com.shopme.common.entity.Order;

@Service
public class MasterOrderReportServices {
	private DateFormat dateFormatter;
	
	@Autowired
	private OrderRepository orderRepo;
	
	public List<OrderReportItem> listSalesReportLast28Days() {
		return listSalesReportLastXDays(28);
	}
	
	public List<OrderReportItem> listSalesReportLast7Days() {
		return listSalesReportLastXDays(7);
	}

	private List<OrderReportItem> listSalesReportLastXDays(int days) {
		
		Date endTime = new Date();
		
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(endTime);
		startDate.add(Calendar.DAY_OF_MONTH, -(days - 1));
				
		Date startTime = startDate.getTime();
		
		dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
				
		return listOrderReportByDateRange(startTime, endTime, "days");
	}

	public List<OrderReportItem> listSalesReportLastYear() {
		return listSalesReportLastXMonths(12);
	}
	
	public List<OrderReportItem> listSalesReportLast6Months() {
		return listSalesReportLastXMonths(6);
	}
	
	private List<OrderReportItem> listSalesReportLastXMonths(int months) {
		System.out.println("listSalesReportLast6Months");
		
		Date endTime = new Date();
		
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(endTime);
		startDate.add(Calendar.MONTH, -(months - 1));
				
		Date startTime = startDate.getTime();
		
		System.out.println("Start time: " + startTime);
		System.out.println("End time: " + endTime);
		
		dateFormatter = new SimpleDateFormat("yyyy-MM");
				
		return listOrderReportByDateRange(startTime, endTime, "months");		
	}
	
	private List<OrderReportItem> listOrderReportByDateRange(Date startTime, Date endTime, String periodType) {
				
		List<Order> listOrders = orderRepo.findByOrderTimeBetween(startTime, endTime);
		
		printRawOrderList(listOrders);
		
		List<OrderReportItem> listReportItems = createOrderReportList(startTime, endTime, periodType);
		updateOrderReportList(listOrders, listReportItems);
		
		printReportItemsList(listReportItems);
		
		return listReportItems;
	}

	private void printRawOrderList(List<Order> listOrders) {
		System.out.println("==== Raw Order List Before Processing ===");
		
		int count = 1;		
		for (Order order : listOrders) {			
			System.out.println(count++ + " | " + order.getId() + " | " + order.getOrderTime()
				+ " | " + order.getTotal() + " | " + order.getCost());		
		}
		
		System.out.println("==== END Raw Order List Before Processing ===");
	}
	
	private void updateOrderReportList(List<Order> listOrders, List<OrderReportItem> listReportItems) {
		for (Order order : listOrders) {			
			String orderDateOnly = dateFormatter.format(order.getOrderTime());
			OrderReportItem reportItem = new OrderReportItem(orderDateOnly);
			
			int itemIndex = listReportItems.indexOf(reportItem);
			
			if (itemIndex >= 0) {
				reportItem = listReportItems.get(itemIndex);
				
				reportItem.addGrossSales(order.getTotal());
				reportItem.addNetSales(order.getSubtotal() - order.getCost());
				reportItem.increaseOrderCount();
			}
		}	
		
	}

	private void printReportItemsList(List<OrderReportItem> listReportItems) {
		System.out.println("==== Order Report Items ===");
		
		for (OrderReportItem report : listReportItems) {
			System.out.println(report);
		}
		
		System.out.println("==== END Order Report Items ===");
	}
	
	private List<OrderReportItem> createOrderReportList(Date startTime, Date endTime, String periodType) {
		List<OrderReportItem> listReportItems = new ArrayList<>();
		
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(startTime);
		
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(endTime);		
		
		Date currentDate = startDate.getTime();
		String currentDateOnly = dateFormatter.format(currentDate);
		
		listReportItems.add(new OrderReportItem(currentDateOnly));
		
		do {
			if (periodType.equals("days")) {
				startDate.add(Calendar.DAY_OF_MONTH, 1);
			} else if (periodType.equals("months")) {
				startDate.add(Calendar.MONTH, 1);
			}
			
			currentDate = startDate.getTime();
			currentDateOnly = dateFormatter.format(currentDate);
			
			listReportItems.add(new OrderReportItem(currentDateOnly));
			
		} while (startDate.before(endDate));

		return listReportItems;
	}
}
