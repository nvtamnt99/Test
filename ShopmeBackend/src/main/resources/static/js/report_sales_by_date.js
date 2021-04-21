// Report Sales by Date
$(document).ready(function(){
	$(".btn-sales-by-date").on("click", function(e) {
		
		$(".btn-sales-by-date").each(function(e) {
			$(this).removeClass('btn-primary').addClass('btn-light');
		});
		
		$(this).removeClass('btn-light').addClass('btn-primary');
		
		period = $(this).attr('period');		
		loadReportSalesByDate(period);
	});
});


function loadReportSalesByDate(period) {
	days = getDays(period);
	url = contextPath + "reports/sales_by_date/" + period;

	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Date');
	data.addColumn('number', 'Gross Sales');
	data.addColumn('number', 'Net Sales');
	data.addColumn('number', 'Orders');
	
	totalGrossSales = 0.0;
	totalNetSales = 0.0;
	totalOrders = 0;
	
	$.get(url, function(reportJson) {

		$.each(reportJson, function(index, reportItem) {
			data.addRows([[reportItem.identifier, reportItem.grossSales, reportItem.netSales, reportItem.ordersCount]]);
			totalGrossSales += parseFloat(reportItem.grossSales);
			totalNetSales += parseFloat(reportItem.netSales);
			totalOrders += parseInt(reportItem.ordersCount);
		});		
		
		var options = {
				title: getChartTitle(period),
				'height': 360,
				legend: {position: 'top'},
	        series: {
	            0: {targetAxisIndex: 0},
	            1: {targetAxisIndex: 0},
	            2: {targetAxisIndex: 1}
	          },
	          vAxes: {
	            // Adds titles to each axis.
	            0: {title: 'Sales Amount', format: 'currency'},
	            1: {title: 'Number of Orders'}
	          }		
		};
		
	    var formatter = new google.visualization.NumberFormat({
	    	prefix: '$'
	    });
	    formatter.format(data, 1);
	    formatter.format(data, 2);
		
		var salesChart = new google.visualization.ColumnChart(document.getElementById('chart_sales_by_date'));
		salesChart.draw(data, options);	
		
		$("#textTotalGrossSales1").text("$" + $.number(totalGrossSales, 2));
		$("#textTotalNetSales1").text("$" + $.number(totalNetSales, 2));
		
		$("#textAvgGrossSales1").text("$" + $.number(totalGrossSales / days, 2));
		$("#textAvgNetSales1").text("$" + $.number(totalNetSales / days, 2));
		
		$("#textTotalOrders").text(totalOrders);
		
	});
	
	
}

function getDays(period) {
	if (period == 'last_7_days') return 7;
	if (period == 'last_28_days') return 28;
	if (period == 'last_6_months') return 6;
	if (period == 'last_year') return 12;
	
	return 7;
}

function getChartTitle(period) {
	if (period == 'last_7_days') return "Sales in Last 7 Days";
	if (period == 'last_28_days') return "Sales in Last 28 Days";
	if (period == 'last_6_months') return "Sales in Last 6 Months";
	if (period == 'last_year') return "Sales in Last Year";
	
	return "";
}