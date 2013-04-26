function setSource() {
	var a = window.location.href.split("#");
	if (a.length == 2) {
		document.cookie = "source=" + escape(a[1]);
	}
}

function getSource() {
	if (document.cookie) {
		var a = document.cookie.split("source=");
		if (a.length >= 2) {
			var b = a[1].split(';');
			return unescape(b[0]);
		}
	}
	return "";
}

var theForm;

function sendRequest(jqueryForm) {
	if (jqueryForm.hasClass("disabled")) {
		return;
	}

	jqueryForm.addClass("disabled");
	$(".trylink").addClass("disabled");
	$("input").addClass("disabled");
	$(jqueryForm.find(".submit")).val("Please Wait...");

	jqueryForm.children(".control-group").removeClass("error");

	var emailEntered = jqueryForm.find(".input").val();

	theForm = jqueryForm;
	$.post("/site/request-invite-servlet", {
		"email" : emailEntered,
		"tracking" : getSource()
	}, afterRequest, "text");

	$("#main-trybox").popover("hide");

}

function afterRequest(data) {
	if (data == "true") {
		var emailEntered = theForm.find(".input").val();
		$(".trybox input").hide();
		$(".trybox h2")
				.text(
						"Thanks for creating an account for our beta. We will soon send your login information to "
								+ emailEntered + ".");
	} else {
		if (data == "false") {
			theForm.children(".help-inline").text(
					"Uh Oh! This isn't a valid email.");
		} else if (data == "captcha") {
			theForm
					.children(".help-inline")
					.text(
							"This IP is issuing a high volume of requests.  Please try again in 60 seconds.");
		} else {
			theForm.children(".help-inline").text(
					"Oh no! Something is wrong. Please report this error.");
		}

		theForm.removeClass("disabled");
		$(".trylink").removeClass("disabled");
		$("input").removeClass("disabled");

		theForm.find(".submit").val("Create Account");

		theForm.children(".control-group").addClass("error");
	}
}

$(document).ready(function() {

	$(".trybox").submit(function() {
		sendRequest($(this));
		return false;
	});

});
