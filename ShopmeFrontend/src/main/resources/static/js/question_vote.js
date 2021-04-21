$(document).ready(function() {
	$(".link-vote").on("click", function(e) {
		e.preventDefault();
		voteQuestion($(this));
	});		
});

function voteQuestion(link) {
	url = link.attr('href');
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(crsfHeaderName, csrfValue);
		}
	}).done(function(response) {
		$("#modalTitle").text("Vote Question");
		if (response.includes("voted")) {
			$("#myModal").on("hide.bs.modal", function(e) {
				refreshQuestionVotes(link, response);	
			});			
		}
		
		$("#modalBody").text(response);
		$('#myModal').modal();
	}).fail(function() {
		$("#modalTitle").text("Error");
		$("#modalBody").text("Failed to vote question. May be you have not logged in or session timed out. Try to login and post question again.");
		$('#myModal').modal();
	});			
}

function refreshQuestionVotes(link, response) {
	questionId = link.attr("qid");
	url = contextPath + "getquestionvote/" + questionId; 
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr) {
			xhr.setRequestHeader(crsfHeaderName, csrfValue);
		}
	}).done(function(voteNumber) {
		$("#v" + questionId).text(voteNumber);
		voteDownLink = $("#vd-" + questionId);
		voteUpLink = $("#vu-" + questionId);
		
		if (response.includes("successfully voted up")) {
			link.removeClass("icon-dark").addClass("icon-green");
			link.attr("title", "Undo vote up this question");
			voteDownLink.removeClass("icon-green").addClass("icon-dark");
		} else if (response.includes("successfully voted down")) {
			link.removeClass("icon-dark").addClass("icon-green");
			link.attr("title", "Undo vote down this question");
			voteUpLink.removeClass("icon-green").addClass("icon-dark");
		} else if (response.includes("unvoted down")) {
			link.attr("title", "Vote down this question");
			voteDownLink.removeClass("icon-green").addClass("icon-dark");
		} else if (response.includes("unvoted up")) {
			link.attr("title", "Vote up this question");
			voteUpLink.removeClass("icon-green").addClass("icon-dark");
		}
		
	}).fail(function() {
		$("#modalTitle").text("Error");
		$("#modalBody").text("Failed update votes for that question");
		$('#myModal').modal();
	});
	
	
}