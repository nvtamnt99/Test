$(document).ready(function() {	
	$(".link-remove").on("click", function(e) {
		e.preventDefault();
		removeFromCart($(this));
	});	
	
	$(".linkMinus").on("click", function(evt) {
		evt.preventDefault();
		increaseQuantity($(this));
	});
	
	$(".linkPlus").on("click", function(evt) {
		evt.preventDefault();
		decreaseQuantity($(this));
	});	
	
	updateTotal();
});

function removeFromCart(link) {
	url = link.attr("href");
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(crsfHeaderName, csrfValue);
		}
	}).done(function(response) {
		$("#modalTitle").text("Shopping Cart");
		if (response.includes("removed")) {
			$("#myModal").on("hide.bs.modal", function(e) {
				rowNumber = link.attr('rowNumber');
				removeProduct(rowNumber);
				updateCountNumbers();
				updateTotal();
			});			
		}
		
		$("#modalBody").text(response);
		$('#myModal').modal();
	}).fail(function() {
		$("#modalTitle").text("Shopping Cart");
		$("#modalBody").text("Error while removing product from shopping cart.");
		$('#myModal').modal();
	});	
}

function removeProduct(rowNumber) {
	rowId = "row" + rowNumber;
	$("#" + rowId).remove();
}

function updateCountNumbers() {
	$(".div-count").each(function (index, element) {
		element.innerHTML = "" + (index + 1);
	});
}

function increaseQuantity(link) {
	productId = link.attr("pid");
	qtyInput = $("#quantity" + productId);
	newQty = parseInt(qtyInput.val()) - 1;
	if (newQty > 0) {
		qtyInput.val(newQty);
		updateQuantity(productId, newQty);
	}
}

function decreaseQuantity(link) {
	productId = link.attr("pid");
	qtyInput = $("#quantity" + productId);		
	newQty = parseInt(qtyInput.val()) + 1;
	if (newQty < 10) {
		qtyInput.val(newQty);
		updateQuantity(productId, newQty);
	}
}

function updateQuantity(productId, quantity) {
	url = contextPath + "cart/update/" + productId + "/" + quantity;
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(crsfHeaderName, csrfValue);
		}
	}).done(function(newSubtotal) {
		updateSubtotal(newSubtotal, productId);
		updateTotal();
	}).fail(function() {
		$("#modalTitle").text("Shopping Cart");
		$("#modalBody").text("Error while updating quantity.");
		$('#myModal').modal();
	});	
}

function updateSubtotal(newSubtotal, productId) {
	formattedSubtotal = $.number(newSubtotal, 2);
	
	$("#subtotal" + productId).text(formattedSubtotal);
}

function updateTotal() {
	total = 0.0;
	$(".productsubtotal").each(function(index, element) {
		total = total + parseFloat(element.innerHTML);
	});
	
	if (total == 0.0) {
		hideSectionTotal();
	} else {
		formattedTotal = $.number(total, 2);
		$("#total").text(formattedTotal);
	}
}

function hideSectionTotal() {
	$("#sectionTotal").hide();
	$("#sectionEmptyCart").removeClass("d-none");
}