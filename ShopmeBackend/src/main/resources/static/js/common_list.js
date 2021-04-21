function clearFilter() {
	window.location = moduleURL;	
}
	
function showDeleteConfirmModal(link, entityName) {
	$("#yesButton").attr("href", link.attr("href"));
	idValue = link.attr("rowId");
	$("#confirmText").text("Are you sure you want to delete this " + entityName + " " + idValue + "?");
	$("#confirmModal").modal();		
}