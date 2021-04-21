var buttonLoad4States;
var dropdownCountry4States;
var buttonAddState;
var buttonUpdateState;
var buttonDeleteState;
var fieldStateName;
var dropdownStates;
var labelStateName;

$(document).ready(function() {
	buttonLoad4States = $("#buttonLoadCountriesForStates");
	dropdownCountry4States = $("#dropdownCountriesForStates");
	buttonAddState = $("#buttonAddState");
	buttonUpdateState = $("#buttonUpdateState");
	buttonDeleteState = $("#buttonDeleteState");
	fieldStateName = $("#fieldStateName");
	dropdownStates = $("#dropdownStates");
	labelStateName = $("#labelStateName");
	
	buttonLoad4States.click(function() {			
		loadCountries4States();
	});	
	
	buttonAddState.click(function() {
		if (buttonAddState.val() == "Add") {
			addState();
		} else {
			changeFormToNewState();
		}		
	});	
	
	dropdownCountry4States.on("change", function() {
		loadStates4Country();
	});
	
	dropdownStates.on("change", function() {
		changeFormStateToSelectedState();
	});	
	
	buttonUpdateState.click(function() {
		updateState();
	});
	
	buttonDeleteState.click(function() {
		deleteState();
	});	
});

function loadCountries4States() {
	url = contextPath + "countries/list";
	
	$.get(url, function(responseJson) {
		dropdownCountry4States.empty();
		
		$.each(responseJson, function(index, country) {
			$("<option>").val(country.id).text(country.name).appendTo(dropdownCountry4States);
		});
	}).done(function() {
		buttonLoad4States.val("Refresh Country List");
		changeFormToNewState();
		showToastMessage("All countries have been loaded");
	})
}

function loadStates4Country() {
	selectedCountry = $("#dropdownCountriesForStates option:selected");
	countryId = selectedCountry.val();
	
	url = contextPath + "states/list/country/" + countryId;
	
	$.get(url, function(responseJson) {		
		dropdownStates.empty();
		
		$.each(responseJson, function(index, state) {
			$("<option>").val(state.id).text(state.name).appendTo(dropdownStates);
		});
	}).done(function() {
		changeFormToNewState();
		showToastMessage("All states have been loaded for country " + selectedCountry.text());
	})	
}

function addState() {
	url = contextPath + "states/save";
	stateName = fieldStateName.val();
	selectedCountry = $("#dropdownCountriesForStates option:selected");
	countryId = selectedCountry.val();
	countryName = selectedCountry.text();
	
	jsonData = {name: stateName, country: {id: countryId, name: countryName}};
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(crsfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(stateId) {
		selectNewlyAddedState(stateName, stateId);
		showToastMessage("The new state has been added");
	}).fail(function() {
		showToastMessage("ERROR: Could not connect to the server");
	});			
}

function selectNewlyAddedState(stateName, stateId) {
	$("<option>").val(stateId).text(stateName).appendTo(dropdownStates);
	$("#dropdownStates option[value='" + stateId + "']").prop("selected", true);
	fieldStateName.val("").focus();	
}

function changeFormStateToSelectedState() {
	selectedStateName = $("#dropdownStates option:selected").text();		
	labelStateName.text("Selected State: ");
	fieldStateName.val(selectedStateName);
	buttonAddState.prop("value", "New");
	buttonUpdateState.prop("disabled", false);
	buttonDeleteState.prop("disabled", false);		
}

function changeFormToNewState() {
	buttonAddState.val("Add");
	labelStateName.text("State Name: ");
	buttonUpdateState.prop("disabled", true);
	buttonDeleteState.prop("disabled", true);		
	fieldStateName.val("").focus();
}

function updateState() {
	url = contextPath + "states/save";
	stateId = dropdownStates.val();
	stateName = fieldStateName.val();
	selectedCountry = $("#dropdownCountriesForStates option:selected");
	countryId = selectedCountry.val();
	countryName = selectedCountry.text();
	
	jsonData = {id: stateId, name: stateName, country: {id: countryId, name: countryName}};
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(crsfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(stateId) {
		$("#dropdownStates option:selected").text(stateName);
		changeFormToNewState();
		showToastMessage("The new state has been updated");
	}).fail(function() {
		showToastMessage("ERROR: Could not connect to the server");
	});			
}

function deleteState() {
	stateId = dropdownStates.val();
	url = contextPath + "states/delete/" + stateId;

	$.get(url).done(function() {			
		$("#dropdownStates option[value='" + stateId + "']").remove();
		changeFormToNewState();
		
		showToastMessage("The state has been deleted");
	}).fail(function() {
		showToastMessage("ERROR: Could not connect to the server");
	});	
}