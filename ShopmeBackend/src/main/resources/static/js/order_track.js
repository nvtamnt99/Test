$(document).ready(function() {
	// this is to enable event handler for elements that are added later
	$("#trackList").on("click", ".link-remove-track", function(e) {
		e.preventDefault();
		deleteTrack($(this));
		updateCountNumbers();
	});
	
	$("#track").on("click", "#addTrack", function(e) {
		e.preventDefault();
		addNewTrackRecord();
	});
});

function deleteTrack(link) {
	rowNumber = link.attr('rowNumber');
	rowId = "rowTrack" + rowNumber;
	$("#" + rowId).remove();	
	
}

function updateCountNumbers() {
	$(".div-count-track").each(function (index, element) {
		element.innerHTML = "" + (index + 1);
	});
}

function addNewTrackRecord() {
	nextCount = $(".track-id-hidden").length + 1;
	htmlCode = generateTrackCode(nextCount);
	
	$("#trackList").append(htmlCode);
}

function generateTrackCode(nextCount) {
	rowId = "rowTrack" + nextCount;
	currentDateTime = formateCurrentDateTime();
	
	trackCode = `
			<div class="row border rounded p-1" id="${rowId}">
				<input type="hidden" name="trackId" value="0" class="track-id-hidden" />
				<div class="col-2">
					<div class="div-count-track">${nextCount}</div>
					<div class="mt-1"><a class="fas fa-trash icon-dark link-remove-track" href="" rowNumber="${nextCount}"></a></div>					
				</div>				
				
				<div class="col-10">
				  <div class="form-group row">
				    <label class="col-form-label">Time:</label>
				    <div class="col">
						<input type="datetime-local" name="trackDate" value="${currentDateTime}" class="form-control" required
							style="max-width: 300px"/>						
				    </div>
				  </div>					
				<div class="form-group row">  
				<label class="col-form-label">Status:</label>
				<div class="col">
					<select name="trackStatus" class="form-control" required style="max-width: 150px">
			`;
	  
	trackCode += $("#fixedTrackStatusSection").clone().html();
	
	trackCode += `
				      </select>						
				    </div>
				  </div>
				  <div class="form-group row">
				    <label class="col-form-label">Notes:</label>
				    <div class="col">
						<textarea rows="2" cols="10" class="form-control" name="trackNotes" style="max-width: 300px" required></textarea>
				    </div>
				  </div>
				  
				</div>				
			</div>	
	`;
	
	return trackCode;
}

function formateCurrentDateTime() {
	date = new Date();
	year = date.getFullYear();
	month = date.getMonth() + 1;
	day = date.getDate();
	hour = date.getHours();
	minute = date.getMinutes();
	second = date.getSeconds();
	
	if (month < 10) month = "0" + month;
	if (day < 10) day = "0" + day;
	
	if (hour < 10) hour = "0" + hour;
	if (minute < 10) minute = "0" + minute;
	if (second < 10) second = "0" + second;
	
	return year + "-" + month + "-" + day + "T" + hour + ":" + minute + ":" + second;
	
}