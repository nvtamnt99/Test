$(document).ready(function() {
	$(".link-vote-review").on("click", function(e) {
		e.preventDefault();
		voteReview($(this));
	});		
});

function voteReview(link) {
	url = link.attr('href');
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(crsfHeaderName, csrfValue);
		}
	}).done(function(response) {
		$("#modalTitle").text("Vote Review");
		if (response.includes("voted")) {
			$("#myModal").on("hide.bs.modal", function(e) {
				refreshReviewVotes(link, response);	
			});			
		}
		
		$("#modalBody").text(response);
		$('#myModal').modal();
	}).fail(function() {
		$("#modalTitle").text("Error");
		$("#modalBody").text("Failed to vote review. May be you have not logged in or session timed out. Try to login and vote review again.");
		$('#myModal').modal();
	});			
}

function refreshReviewVotes(link, response) {
	reviewId = link.attr("rid");
	url = contextPath + "getreviewvote/" + reviewId; 
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(crsfHeaderName, csrfValue);
		}
	}).done(function(voteNumber) {
		$("#vr" + reviewId).text(voteNumber);
		voteDownLink = $("#vrd-" + reviewId);
		voteUpLink = $("#vru-" + reviewId);
		
		if (response.includes("successfully voted up")) {
			link.removeClass("icon-dark").addClass("icon-green");
			link.attr("title", "Undo vote up this review");
			voteDownLink.removeClass("icon-green").addClass("icon-dark");
		} else if (response.includes("successfully voted down")) {
			link.removeClass("icon-dark").addClass("icon-green");
			link.attr("title", "Undo vote down this review");
			voteUpLink.removeClass("icon-green").addClass("icon-dark");
		} else if (response.includes("unvoted down")) {
			link.attr("title", "Vote down this review");
			voteDownLink.removeClass("icon-green").addClass("icon-dark");
		} else if (response.includes("unvoted up")) {
			link.attr("title", "Vote up this review");
			voteUpLink.removeClass("icon-green").addClass("icon-dark");
		}
		
	}).fail(function() {
		$("#modalTitle").text("Error");
		$("#modalBody").text("Failed update votes for that review");
		$('#myModal').modal();
	});
	
	
}