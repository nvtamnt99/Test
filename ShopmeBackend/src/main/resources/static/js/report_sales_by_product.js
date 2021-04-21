// Report Sales by Product
$(document).ready(function(){
	$(".btn-sales-by-product").on("click", function(e) {
		
		$(".btn-sales-by-product").each(function(e) {
			$(this).removeClass('btn-primary').addClass('btn-light');
		});
		
		$(this).removeClass('btn-light').addClass('btn-primary');
		
		period = $(this).attr('period');		
		loadReportSalesByProduct(period);
	});
});


function loadReportSalesByProduct(period) {
	days = getDays(period);
	url = contextPath + "reports/product/" + period;

	var data = new google.visualization.DataTable();
	data.addColumn('string', 'Product');
	data.addColumn('number', 'Quantity');
	data.addColumn('number', 'Gross Sales');
	data.addColumn('number', 'Net Sales');
	
	totalGrossSales = 0.0;
	totalNetSales = 0.0;
	totalProducts = 0;
	
	$.get(url, function(reportJson) {

		$.each(reportJson, function(index, reportItem) {
			data.addRows([[reportItem.identifier, reportItem.productsCount, reportItem.grossSales, reportItem.netSales]]);
			totalGrossSales += parseFloat(reportItem.grossSales);
			totalNetSales += parseFloat(reportItem.netSales);
			totalProducts += parseInt(reportItem.productsCount);
		});		
		
		var options = {
				title: getChartTitle(period),
				'height': 360,
				showRowNumber: true,
				page: 'enable',
				sortColumn: 2,
				sortAscending: false,
		};
		
	    var formatter = new google.visualization.NumberFormat({
	    	prefix: '$'
	    });
	    formatter.format(data, 2);
	    formatter.format(data, 3);		
		
		var salesChart = new google.visualization.Table(document.getElementById('chart_sales_by_product'));
		salesChart.draw(data, options);	
		
		$("#textTotalGrossSales3").text("$" + $.number(totalGrossSales, 2));
		$("#textTotalNetSales3").text("$" + $.number(totalNetSales, 2));
		
		$("#textAvgGrossSales3").text("$" + $.number(totalGrossSales / days, 2));
		$("#textAvgNetSales3").text("$" + $.number(totalNetSales / days, 2));
		
		$("#textTotalProducts2").text(totalProducts);		
		
		
	});
	
	
}