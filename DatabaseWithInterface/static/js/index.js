$(document).ready(function(){
	$("input:checkbox").on('click', function() {
		var $box = $(this);

		var group = "input:checkbox[name='" + $box.attr("name") + "']";
		$(group).prop("checked", false);
		$box.prop("checked", true);
		
		if (!($box.attr('id') == 'admin' || $box.attr('id') == 'user')){
			var group = "input:text";
			$(group).prop("disabled", true);

			var group = ".paper-operation";
			$(group).prop("hidden", true);

			var group = ".author-operation";
			$(group).prop("hidden", true);

			var group = ".topic-operation";
			$(group).prop("hidden", true);
		}

		if ($box.is(":checked")) {

			if ($box.attr('id') == 'author-name'){
				$("#author-name-box").prop('disabled', false);
			} else if ($box.attr('id') == 'topic-name'){
				$("#topic-name-box").prop('disabled', false);
			}  else if ($box.attr('id') == 'search-word'){
				$("#search-word-box").prop('disabled', false);
			}  else if ($box.attr('id') == 'co-author-name'){
				$("#co-author-name-box").prop('disabled', false);
			}
		
			if ($box.attr('id') == 'create-paper'){
				$('#create-paper-options').prop('hidden', false);
			} else if ($box.attr('id') == 'update-paper') {
				$('#update-paper-options').prop('hidden', false);
			} else if ($box.attr('id') == 'delete-paper') {
				$('#delete-paper-options').prop('hidden', false);
			} else if ($box.attr('id') == 'create-author' || $box.attr('id') == 'delete-author') {
				$('#creaete-delete-author-options').prop('hidden', false);
			} else if ($box.attr('id') == 'update-author') {
				$('#update-author-options').prop('hidden', false);
			} else if ($box.attr('id') == 'create-topic') {
				$('#create-topic-options').prop('hidden', false);
			} else if ($box.attr('id') == 'update-topic') {
				$('#update-topic-options').prop('hidden', false);
			} else if ($box.attr('id') == 'delete-topic') {
				$('#delete-topic-options').prop('hidden', false);
			} 
		
		} else {
			$box.prop("checked", false);
		}

		$('.my-argument').prop('disabled', false);

	});

});

function makeQuery(clickedButton) {
	
	context = {};

    var group = $('input:checked');
	context['user_type'] = group[0]['id'];
    context['operation'] = group[1]['id'];
    
    operation_array = ['create-paper', 'update-paper', 'delete-paper', 'create-author', 'update-author', 'delete-author', 'create-topic', 'update-topic', 'delete-topic'];
    if(context['user_type'] == 'user' && jQuery.inArray(context['operation'], operation_array) != -1){
        alert('User Can Not Make This Operation');
        return;
    }

	if($('#create-paper').is(':checked')){
		context['arguments'] = {
			'author': $('#c-p-author-filed')[0]['value'],
			'title': $('#c-p-title-filed')[0]['value'],
			'abstract': $('#c-p-abstract-filed')[0]['value'],
			'topic': $('#c-p-topic-filed')[0]['value'],
			'result': $('#c-p-result-filed')[0]['value'],
		};
	} else if($('#update-paper').is(':checked')){
		context['arguments'] = {
			'old-title': $('#u-p-old-title-filed')[0]['value'],
			'author': $('#u-p-author-filed')[0]['value'],
			'title': $('#u-p-title-filed')[0]['value'],
			'abstract': $('#u-p-abstract-filed')[0]['value'],
			'topic': $('#u-p-topic-filed')[0]['value'],
			'result': $('#u-p-result-filed')[0]['value'],
		};
	} else if($('#delete-paper').is(':checked')){
		context['arguments'] = {'title': $('#d-p-author-filed')[0]['value']};
	}  else if ($('#create-author').is(':checked') || $('#delete-author').is(':checked')){
		context['arguments'] = {
			'name': $('#c-d-a-name-filed')[0]['value'],
			'surname': $('#c-d-a-sur-filed')[0]['value'],
		}; 
	} else if($('#update-author').is(':checked')){
		context['arguments'] = {
			'old-name': $('#u-a-old-name-filed')[0]['value'],
			'old-surname': $('#u-a-old-sur-filed')[0]['value'],
			'name': $('#u-a-name-filed')[0]['value'],
			'surname': $('#u-a-sur-filed')[0]['value'],
		}; 
	} else if ($('#create-topic').is(':checked')){
		context['arguments'] = {'name': $('#c-t-name-filed')[0]['value']}; 
	} else if ($('#update-topic').is(':checked')){
		context['arguments'] = {
			'old-name': $('#u-t-old-name-filed')[0]['value'],
			'name': $('#u-t-name-filed')[0]['value'],
		};
	} else if ($('#delete-topic').is(':checked')){
		context['arguments'] = {'name': $('#d-t-name-filed')[0]['value'],};
	} else if ($('#author-name').is(':checked')){
		context['arguments'] = {'name': $('#author-name-box')[0]['value']};
	} else if ($('#topic-name').is(':checked')){
		context['arguments'] = {'topic': $('#topic-name-box')[0]['value']};
	} else if ($('#search-word').is(':checked')){
		context['arguments'] = {'word': $('#search-word-box')[0]['value']};
	} else if ($('#co-author-name').is(':checked')){
		context['arguments'] = {'name': $('#co-author-name-box')[0]['value']};
	} else {
		context['arguments'] = {};
	}

	console.log(context)

	$.ajax({
		type: "POST",
		url: "/makeQuery",
		data: context,
		success: function (response) {
			console.log("success");
			$("#query-result").empty();
			$("#query-result").append(response);
		},
		error: function (response){
			console.log("failed");
		}
	});

	
}
