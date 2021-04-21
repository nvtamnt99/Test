$(document).ready(function() {
	formatAmounts();
	
	// this is to enable event handler for elements that are added later
	$("#productList").on("click", ".link-remove", function(e) {
		e.preventDefault();
		deleteProduct($(this));
		updateOrderAmounts();
	});	

	$("#productList").on("change", ".quantity-input", function(e) {
		updateSubtotalWhenQuantityChanged($(this));
		updateOrderAmounts();
	});		
	
	$("#productList").on("change", ".price-input", function(e) {
		updateSubtotalWhenPriceChanged($(this));
		updateOrderAmounts();
	});
	
	$("#productList").on("change", ".cost-input", function(e) {
		updateOrderAmounts();
	});

	$("#productList").on("change", ".ship-input", function(e) {
		updateOrderAmounts();
	});
	
	formatProductCosts();
	
	formatProductShips();
	
	formatUnitPrices();
	
	formatProductSubtotals();
	
	$("#products").on("click", "#addProduct", function(e) {
		e.preventDefault();
		link = $(this);
		url = link.attr('href');
		
		$('#myModal').on('shown.bs.modal', function() {
		    $(this).find('iframe').attr('src', url)
		});
		
		$('#myModal').on('hidden.bs.modal', function() {
			updateOrderAmounts();			
		});
		
		$('#myModal').modal();
		
	});
});

function formatProductShips() {
	$(".ship-input").each(function(e) {
		shipInput = $(this);
		shipInput.val($.number(shipInput.val(), 2));
	});	
}

function formatProductCosts() {
	$(".cost-input").each(function(e) {
		costInput = $(this);
		costInput.val($.number(costInput.val(), 2));
	});	
}

function formatUnitPrices() {
	$(".price-input").each(function(e) {
		priceInput = $(this);
		priceInput.val($.number(priceInput.val(), 2));
	});	
}

function formatProductSubtotals() {
	$(".subtotal-output").each(function(e) {
		subtotal = $(this);
		subtotal.val($.number(subtotal.val(), 2));
	});	
}

function deleteProduct(link) {
	rowNumber = link.attr('rowNumber');
	rowId = "row" + rowNumber;
	$("#" + rowId).remove();	
	
	updateCountNumbers();
}

function updateCountNumbers() {
	$(".div-count").each(function (index, element) {
		element.innerHTML = "" + (index + 1);
	});
}

function updateSubtotalWhenQuantityChanged(input) {
	rowNumber = input.attr('rowNumber');
	quantityValue = input.val();
	priceValue = parseCurrencyValue($("#price" + rowNumber));
	
	newSubtotal = parseFloat(quantityValue) * parseFloat(priceValue);
	formattedSubtotal = $.number(newSubtotal, 2);
	
	$("#subtotal" + rowNumber).val(formattedSubtotal);	
}

function updateSubtotalWhenPriceChanged(input) {
	rowNumber = input.attr('rowNumber');
	priceValue = parseCurrencyValue(input);
	
	quantityValue = $("#quantity" + rowNumber).val();
	newSubtotal = parseFloat(quantityValue) * parseFloat(priceValue);
	formattedSubtotal = $.number(newSubtotal, 2);
	
	$("#subtotal" + rowNumber).val(formattedSubtotal);	
}

function formatAmounts() {
	orderSubtotal = $("#subtotal").val();
	$("#subtotal").val($.number(orderSubtotal, 2));
	
	shippingCost = $("#shippingCost").val();
	$("#shippingCost").val($.number(shippingCost, 2));
	
	total = $("#total").val();
	$("#total").val($.number(total, 2));
	
	cost = $("#cost").val();
	$("#cost").val($.number(cost, 2));
}

function updateOrderAmounts() {
	orderSubtotal = 0.0;
	
	$(".subtotal-output").each(function(e) {
		productSubtotal = parseCurrencyValue($(this));
		orderSubtotal += parseFloat(productSubtotal); 
	});
	
	$("#subtotal").val($.number(orderSubtotal, 2));
	
	orderTotal = 0.0;
	
	shippingCost = 0.0;
	
	$(".ship-input").each(function(e) {
		productShip = parseCurrencyValue($(this));
		shippingCost += parseFloat(productShip); 
	});
	
	$("#shippingCost").val($.number(shippingCost, 2));
		
	tax = parseCurrencyValue($("#tax"));
	
	orderTotal = orderSubtotal + shippingCost + tax;
	
	$("#total").val($.number(orderTotal, 2));
	
	totalCost = 0.0;
	
	$(".cost-input").each(function(e) {
		rowNumber = ($(this)).attr('rowNumber');
		quantityValue = $("#quantity" + rowNumber).val();
		
		productCost = parseCurrencyValue($(this));		
		totalCost += parseFloat(productCost) * parseInt(quantityValue); 
	});
	
	$("#cost").val($.number(totalCost, 2));
}

function parseCurrencyValue(inputField) {
	currencyValue = inputField.val().replace(",", "");
	
	return parseFloat(currencyValue);
}

function addProduct(productId) {
	if (isProductAlreadyAdded(productId)) {
		alert("The productId " + productId + " is already added.");
	} else {
		insertProductCode(productId);
		$('#myModal').modal("hide");		
	}
}

function isProductAlreadyAdded(productId) {
	productExisted = false;
	
	$(".product-id-hidden").each(function(e) {
		aProductId = $(this).val();
		if (aProductId == productId) {
			productExisted = true;
			return;
		}
	});
	
	return productExisted;
}


function insertProductCode(productId) {
	nextCount = $(".product-id-hidden").length + 1;
	
	url = contextPath + "products/get/" + productId;
	
	$.get(url, function(productJson) {
		console.log(productJson);
		
		productImagePath = contextPath.substring(0, contextPath.length - 1) + productJson.imagePath;
		productName = productJson.name;
		productPrice = $.number(productJson.price, 2);
		productCost = $.number(productJson.cost, 2);
		 
		htmlCode = generateProductCode(productId, productName, productCost, productPrice, productImagePath, nextCount);
		
		$("#productList").append(htmlCode);
		
	});
}

function generateProductCode(productId, productName, productCost, productPrice, productImagePath, nextCount) {
	rowId = "row" + nextCount;
	quantityId = "quantity" + nextCount;
	subtotalId = "subtotal" + nextCount;
	priceId = "price" + nextCount;
	
	productCode = `
		<div class="row border rounded p-1" id="${rowId}">
			<input type="hidden" name="productId" value="${productId}" class="product-id-hidden" />
			<input type="hidden" name="detailId" value="0"  />
			<div class="col-1">
				<div class="div-count">${nextCount}</div>
				<div class="mt-1"><a class="fas fa-trash icon-dark link-remove" href="" rowNumber="${nextCount}"></a></div>					
			</div>
			<div class="col-3"><img src="${productImagePath}" class="img-fluid" /></div>
			<div class="col-8-sm">
				<div>
					<b>${productName}</b>
				</div>
				
				  <div class="form-group row ml-3">
				    <label class="col-form-label">Cost:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
				    <div class="col">
				      <input type="text" required class="form-control cost-input" name="productCost"
				        rowNumber="${nextCount}"
				      	value="${productCost}" style="max-width: 140px" />
				    </div>
				  </div>
				  				
			  <div class="form-group row ml-3">
			    <label class="col-form-label">Quantity:</label>
			    <div class="col">
			      <input type="number" step="1" required class="form-control quantity-input" min="1" max="10" 
			      	rowNumber="${nextCount}" id="${quantityId}" 
			      	name="quantity" value="1" style="max-width: 80px" />
			    </div>
			  </div>					

			  <div class="form-group row ml-3">
			    <label class="col-form-label">Price:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
			    <div class="col">
			      <input type="text" required class="form-control price-input" name="price"
			      	rowNumber="${nextCount}"
			      	id="${priceId}" 
			      	value="${productPrice}" style="max-width: 140px" />
			    </div>
			  </div>

			  <div class="form-group row ml-3">
			    <label class="col-form-label">Subtotal:</label>
			    <div class="col">
			      <input type="text" readonly="readonly" class="form-control subtotal-output" name="productSubtotal"
			      	id="${subtotalId}" 
			      	value="${productPrice}" style="max-width: 140px" />
			    </div>
			  </div>
			  
			  <div class="form-group row ml-3">
			    <label class="col-form-label">Ship:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label>
			    <div class="col">
			      <input type="text" required class="form-control ship-input" name="productShip"
			      	rowNumber="${nextCount}" 
			      	value="0" style="max-width: 140px" />
			    </div>
			  </div>			  
			</div>
		</div>	
	`;
	
	return productCode;
}

function processCurrencyFieldsBeforeSubmit() {
	orderCostField = $("#cost");
	orderCostField.val(orderCostField.val().replace(",", ""));
	
	orderSubtotalField = $("#subtotal");
	orderSubtotalField.val(orderSubtotalField.val().replace(",", ""));	
	
	shippingCostField = $("#shippingCost");
	shippingCostField.val(shippingCostField.val().replace(",", ""));	

	orderTaxField = $("#tax");
	orderTaxField.val(orderTaxField.val().replace(",", ""));
	
	orderTotalField = $("#total");
	orderTotalField.val(orderTotalField.val().replace(",", ""));

	$(".price-input").each(function(e) {
		priceInputField = $(this);
		priceInputField.val(priceInputField.val().replace(",", ""));
	});	

	$(".subtotal-output").each(function(e) {
		subtotalField = $(this);
		subtotalField.val(subtotalField.val().replace(",", ""));
	});
	
	$(".cost-input").each(function(e) {
		costField = $(this);
		costField.val(costField.val().replace(",", ""));
	});	
	
	$(".ship-input").each(function(e) {
		shipField = $(this);
		costshipField.val(shipField.val().replace(",", ""));
	});	
	
	return true;
	
}