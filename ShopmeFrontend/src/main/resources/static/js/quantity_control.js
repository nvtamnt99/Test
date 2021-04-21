$(document).ready(function() {	
	
	$(".linkMinus").on("click", function(evt) {
		evt.preventDefault();
		productId = $(this).attr("pid");
		qtyInput = $("#quantity" + productId);
		newQty = parseInt(qtyInput.val()) - 1;
		if (newQty > 0) qtyInput.val(newQty);
	});
	
	$(".linkPlus").on("click", function(evt) {
		evt.preventDefault();
		productId = $(this).attr("pid");
		qtyInput = $("#quantity" + productId);		
		newQty = parseInt(qtyInput.val()) + 1;
		if (newQty < 10) qtyInput.val(newQty);
	});		
});
