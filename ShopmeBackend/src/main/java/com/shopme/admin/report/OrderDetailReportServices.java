package com.shopme.admin.report;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.admin.order.OrderDetailRepository;
import com.shopme.common.entity.OrderDetail;

@Service
public class OrderDetailReportServices {
	@Autowired
	private OrderDetailRepository repo;

	private String groupBy = "category";
	
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}

	public List<OrderReportItem> getReportDataLast7Days() {
		return getReportDataLastXDays(7);
	}
	
	public List<OrderReportItem> getReportDataLast28Days() {
		return getReportDataLastXDays(28);
	}
	
	public List<OrderReportItem> getReportDataLast6Months() {
		return getReportDataLastXMonths(6);
	}

	public List<OrderReportItem> getReportDataLastYear() {
		return getReportDataLastXMonths(12);
	}
	
	private List<OrderReportItem> getReportDataLastXDays(int days) {
		
		Date endTime = new Date();
		
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(endTime);
		startDate.add(Calendar.DAY_OF_MONTH, -(days - 1));
				
		Date startTime = startDate.getTime();
		
		return getReportDataByDateRange(startTime, endTime);
	}
	
	private List<OrderReportItem> getReportDataLastXMonths(int months) {
		Date endTime = new Date();
		
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(endTime);
		startDate.add(Calendar.MONTH, -(months - 1));
				
		Date startTime = startDate.getTime();
		
		return getReportDataByDateRange(startTime, endTime);		
	}
	
	private List<OrderReportItem> getReportDataByDateRange(Date startTime, Date endTime) {
		List<OrderDetail> listOrderDetails = null;
		
		if (groupBy.equals("category")) {
			listOrderDetails = repo.findByOrderDetailCategoryNameTimeBetween(startTime, endTime);
		} else if (groupBy.equals("product")) {
			listOrderDetails = repo.findByOrderDetailProductNameTimeBetween(startTime, endTime);
		}
		
		List<OrderReportItem> listReportData = new ArrayList<>();

		for (OrderDetail aDetail : listOrderDetails) {
			String identifier = "";
			
			if (groupBy.equals("category")) {
				identifier = aDetail.getProduct().getCategory().getName();
			} else if (groupBy.equals("product")) {
				identifier = aDetail.getProduct().getName();
			}
			
			OrderReportItem reportItem = new OrderReportItem(identifier);
			float grossSales = aDetail.getSubtotal()  + aDetail.getShip();
			float netSales = aDetail.getSubtotal() - aDetail.getCost();
			
			int itemIndex = listReportData.indexOf(reportItem);
			
			if (itemIndex >= 0) {
				OrderReportItem existingReportItem = listReportData.get(itemIndex);
				existingReportItem.addGrossSales(grossSales);
				existingReportItem.addNetSales(netSales);
				existingReportItem.addProductCount(aDetail.getQuantity());
				
			} else {
				listReportData.add(new OrderReportItem(identifier, grossSales, netSales, aDetail.getQuantity()));
			}
			
		}
		
		System.out.println("==== Order Report Items - " + groupBy + " ===");
		
		for (OrderReportItem report : listReportData) {
			System.out.println(report);
		}
		
		System.out.println("==== END Order Report Items - " + groupBy + " ===");		
		
		return listReportData;
	}
}
