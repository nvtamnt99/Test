package com.shopme.admin.report;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportRestController {

	@Autowired
	private MasterOrderReportServices masterOrderReportService;
	
	@Autowired
	private OrderDetailReportServices orderDetailReportService;
	
	@GetMapping("/reports/sales_by_date/{period}")
	public List<OrderReportItem> getReportSalesByDate(@PathVariable("period") String period) {
		switch (period) {
		case "last_7_days":
			return masterOrderReportService.listSalesReportLast7Days();
		case "last_28_days":
			return masterOrderReportService.listSalesReportLast28Days();
		case "last_6_months":
			return masterOrderReportService.listSalesReportLast6Months();
		case "last_year":
			return masterOrderReportService.listSalesReportLastYear();			
			
		default:
			return masterOrderReportService.listSalesReportLast7Days();
		}
		
	}
	
	@GetMapping("/reports/{groupBy}/{period}")
	public List<OrderReportItem> getReportSalesByCategory(@PathVariable("groupBy") String groupBy, 
			@PathVariable("period") String period) {
		orderDetailReportService.setGroupBy(groupBy);
		
		switch (period) {
		case "last_7_days":
			return orderDetailReportService.getReportDataLast7Days();
		case "last_28_days":
			return orderDetailReportService.getReportDataLast28Days();
		case "last_6_months":
			return orderDetailReportService.getReportDataLast6Months();
		case "last_year":
			return orderDetailReportService.getReportDataLastYear();			
			
		default:
			return orderDetailReportService.getReportDataLast7Days();
		}
		
	}	
}
