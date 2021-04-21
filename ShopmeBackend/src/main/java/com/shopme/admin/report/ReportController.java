package com.shopme.admin.report;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ReportController {
	
	@GetMapping("/reports/sales")
	public String viewSalesReportHome(Model model) {
		model.addAttribute("pageTitle", "Sales Report");
		return "reports/sales_report";
	}
}
