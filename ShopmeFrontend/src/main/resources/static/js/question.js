$(document).ready(function() {
	$("#buttonPostQuestion").on("click", function(e) {
		e.preventDefault();
		form = $("#formQuestion")[0];
		if (form.checkValidity()) {
			postQuestion();
		} else {
			form.reportValidity();
		}		
	});		
});

function postQuestion() {
	url = contextPath + "post_question/" + productId;
	question = $("#question").val();
	
	jsonData = {questionContent: question};
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(crsfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(response) {
		$("#modalTitle").text("Post Question");
		$("#modalBody").text("Your question has been posted and awaiting for approval.");
		
		$("#myModal").on("hide.bs.modal", function(e) {
			$("#question").val("");	
		});
		
		$('#myModal').modal();
	}).fail(function() {
		$("#modalTitle").text("Error");
		$("#modalBody").text("Failed to submit question. May be you have not logged in or session timed out. Try to login and post question again.");
		$('#myModal').modal();
	});			
}