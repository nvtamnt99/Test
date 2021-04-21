// Report Sales by Category
$(document).ready(function(){
	$(".btn-sales-by-cat").on("click", function(e) {
		
		$(".btn-sales-by-cat").each(function(e) {
			$(this).removeClass('btn-primary').addClass('btn-light');
		});
		
		$(this).removeClass('btn-light').addClass('btn-primary');
		
		period = $(this).attr('period');		
		loadReportSalesByCategory(period);
	});
});


function loadReportSalesByCategory(period) {
	days = getDays(period);
	url = contextPath + "reports/category/" + period;

	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Category');
	data.addColumn('number', 'Gross Sales');
	data.addColumn('number', 'Net Sales');
	
	totalGrossSales = 0.0;
	totalNetSales = 0.0;
	totalProducts = 0;
	
	$.get(url, function(reportJson) {

		$.each(reportJson, function(index, reportItem) {
			data.addRows([[reportItem.identifier, reportItem.grossSales, reportItem.netSales]]);
			totalGrossSales += parseFloat(reportItem.grossSales);
			totalNetSales += parseFloat(reportItem.netSales);
			totalProducts += parseInt(reportItem.productsCount);
		});		
		
		var options = {
				title: getChartTitle(period),
				'height': 360,
				legend: {position: 'right'}
		};
		
	    var formatter = new google.visualization.NumberFormat({
	    	prefix: '$'
	    });
	    formatter.format(data, 1);
	    formatter.format(data, 2);
	    
		var salesChart = new google.visualization.PieChart(document.getElementById('chart_sales_by_category'));
		salesChart.draw(data, options);	
		
		$("#textTotalGrossSales2").text("$" + $.number(totalGrossSales, 2));
		$("#textTotalNetSales2").text("$" + $.number(totalNetSales, 2));
		
		$("#textAvgGrossSales2").text("$" + $.number(totalGrossSales / days, 2));
		$("#textAvgNetSales2").text("$" + $.number(totalNetSales / days, 2));
		
		$("#textTotalProducts1").text(totalProducts);	
		
		
	});
	
	
}