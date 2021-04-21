$(document).ready(function() {
	$('#fileImage').change(function() {
		fileSize = this.files[0].size;
		if (fileSize > 1048576) {
			this.setCustomValidity("You must choose an image less 1 MB!");
			this.reportValidity();
		} else {
			this.setCustomValidity("");
			showImageThumbnail(this);				
		}
	});
	
	$('#buttonCancel').click(function() {
		window.location = moduleURL;			
	});	
});

function showImageThumbnail(fileInput) {
	var file = fileInput.files[0];
	
	var reader = new FileReader();
	
	reader.onload = function(e) {
		$('#thumbnail').attr('src', e.target.result);
	};
	
	reader.readAsDataURL(file);
}

function showModalDialog(title, message) {
	$("#modalTitle").text(title);
	$("#modalBody").text(message);
	$("#modalDialog").modal();
}	

function showErrorModal(message) {
	showModalDialog("Error", message);
}

function showWarningModal(message) {
	showModalDialog("Warning", message);
}