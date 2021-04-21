var dropdownCountries;
var dropdownStates;

$(document).ready(function() {
	dropdownCountries = $("#country");
	dropdownStates = $("#listStates");

	dropdownCountries.on("change", function() {
		loadStates4Country();
		$("#state").val("").focus();
	});		
});

function loadStates4Country() {
	selectedCountry = $("#country option:selected");
	countryId = selectedCountry.val();
	
	url = contextPath + "states/list/country/" + countryId;
	
	$.get(url, function(responseJson) {
		dropdownStates.empty();
		
		$.each(responseJson, function(index, state) {
			$("<option>").val(state.name).text(state.name).appendTo(dropdownStates);
		});
	}).done(function() {
	})	
}	

function checkPasswordMatch(fieldRetypePassword) {
	if (fieldRetypePassword.value != $("#password").val()) {
		fieldRetypePassword.setCustomValidity("Passwords do not match!");
	} else {
		fieldRetypePassword.setCustomValidity("");
	}
}	