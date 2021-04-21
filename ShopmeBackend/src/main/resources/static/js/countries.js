var buttonLoad;
var buttonAdd;
var buttonUpdate;
var buttonDelete;
var dropdownCountry;
var labelCountryName;
var fieldCountryName;

$(document).ready(function() {
	buttonLoad = $("#buttonLoadCountries");
	buttonAdd = $("#buttonAdd");
	buttonUpdate = $("#buttonUpdate");
	buttonDelete = $("#buttonDelete");
	dropdownCountry = $("#dropDownCountries");
	labelCountryName = $("#labelCountrylName");
	fieldCountryName = $("#fieldCountryName");
	
	buttonLoad.click(function() {			
		loadCountries();
	});
	
	buttonAdd.click(function() {
		if (buttonAdd.val() == "Add") {
			addCountry();				
		} else {
			changeFormStateToNew();
		}
	});

	buttonUpdate.click(function() {
		updateCountry();
	});

	buttonDelete.click(function() {
		deleteCountry();
	});
	
	dropdownCountry.on("change", function() {
		changeFormStateToSelectedCountry();
	});
});

function changeFormStateToSelectedCountry() {
	selectedCountryName = $("#dropDownCountries option:selected").text();		
	labelCountryName.text("Selected country: ");
	fieldCountryName.val(selectedCountryName);
	buttonAdd.prop("value", "New");
	buttonUpdate.prop("disabled", false);
	buttonDelete.prop("disabled", false);		
}

function loadCountries() {
	url = contextPath + "countries/list";
	
	$.get(url, function(responseJson) {
		dropdownCountry.empty();
		
		$.each(responseJson, function(index, country) {
			$("<option>").val(country.id).text(country.name).appendTo(dropdownCountry);
		});
	}).done(function() {
		buttonLoad.val("Refresh Country List");
		showToastMessage("All countries have been loaded");
	})
}

function addCountry() {
	url = contextPath + "countries/save";
	countryName = fieldCountryName.val();
	jsonData = {name: countryName};
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(crsfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(countryId) {
		selectNewlyAddedCountry(countryName, countryId);
		showToastMessage("The new country has been added");
	}).fail(function() {
		showToastMessage("ERROR: Could not connect to the server");
	});		
}

function selectNewlyAddedCountry(countryName, countryId) {
	$("<option>").val(countryId).text(countryName).appendTo(dropdownCountry);
	$("#dropDownCountries option[value='" + countryId + "']").prop("selected", true);
	fieldCountryName.val("").focus();		
}

function updateCountry() {
	url = contextPath + "countries/save";
	countryId = dropdownCountry.val();
	countryName = fieldCountryName.val();
	jsonData = {id: countryId, name: countryName};
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(crsfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function() {
		$("#dropDownCountries option:selected").text(countryName);
		changeFormStateToNew();
		
		showToastMessage("The country has been updated");
	}).fail(function() {
		showToastMessage("ERROR: Could not connect to the server");
	});		
}

function deleteCountry() {
	countryId = dropdownCountry.val();
	url = contextPath + "countries/delete/" + countryId;

	$.get(url).done(function() {			
		$("#dropDownCountries option[value='" + countryId + "']").remove();
		changeFormStateToNew();
		
		showToastMessage("The country has been deleted");
	}).fail(function() {
		showToastMessage("ERROR: Could not connect to the server");
	});	
}

function changeFormStateToNew() {
	buttonAdd.val("Add");
	labelCountryName.text("Country Name: ");
	buttonUpdate.prop("disabled", true);
	buttonDelete.prop("disabled", true);		
	fieldCountryName.val("").focus();
}

function showToastMessage(message) {
	$("#toastMessage").text(message);
	$(".toast").toast('show');
}