function addOptionsToAllDecisionComponents(id) {
	for (var index = 0; index < simpleKnowledgeTypes.length; index++) {
		addOptionToAddDecisionComponent(simpleKnowledgeTypes[index], id);
	}
}

function fillAccordion(node) {
	deleteContentOfAccordionEditor();
	var id = node.id;
	showDetails(id, node.summary, node.description, node.type);
	enableLinkingUnlinkedDecisionComponents(id);
	showDetailsOfChildren(id);
	addOptionsToAllDecisionComponents(id);
}

function showDetails(id, summary, description, type) {
	var detailsElement = document.getElementById("Details");
	detailsElement.insertAdjacentHTML('beforeend', '<p>' + type + ' / ' + summary
			+ ' <input type="submit" class="aui-button" id="update" value="Update"/></p>'
			+ '<p><textarea id="form-description" '
			+ 'style="width:99%; height:auto;border: 1px solid rgba(204,204,204,1); ">' + description
			+ '</textarea></p>');
	detailsElement.style.display = "block";
	var updateButton = document.getElementById("update");
	updateButton.addEventListener("click", function() {
		var description = document.getElementById("form-description").value;
		editDecisionKnowledgeElement(id, summary, description, type, function() {
			updateView(id);
		});
	});
}

function enableLinkingUnlinkedDecisionComponents(parentId) {
	getUnlinkedDecisionComponents(
			parentId,
			function(unlinkedDecisionComponents) {
				var insertString = '<select name="linkExistingIssueSearchField" class="select">';
				for (var index = 0; index < unlinkedDecisionComponents.length; index++) {
					insertString += '<option value="' + unlinkedDecisionComponents[index].id + '">'
							+ unlinkedDecisionComponents[index].type + ' / '
							+ unlinkedDecisionComponents[index].summary + '</option>';
				}
				insertString += '</select><input type="button" name="linkExistingIssueButton" id="linkExistingIssueButton" class="aui-button" value="Create Link"/>';
				document.getElementById("Details").insertAdjacentHTML('beforeend', insertString);
				var linkButton = document.getElementById("linkExistingIssueButton");
				linkButton.addEventListener("click", function() {
					var childId = $("select[name='linkExistingIssueSearchField'] option:selected").val();
					createLinkToExistingElement(parentId, childId);
				});
			});
}

function showDetailsOfChildren(parentId) {
	getLinkedDecisionComponents(parentId, function(linkedDecisionComponents) {
		for (var counter = 0; counter < linkedDecisionComponents.length; counter++) {
			var child = linkedDecisionComponents[counter];
			showDetailsOfSingleChild(child);
		}
	});
}

function showDetailsOfSingleChild(decisionKnowledgeElement) {
	var type = decisionKnowledgeElement.type;
	for (var i = 0; i < simpleKnowledgeTypes.length; i++) {
		if (simpleKnowledgeTypes[i].toLocaleLowerCase() === type.toLocaleLowerCase()) {
			document.getElementById(simpleKnowledgeTypes[i]).insertAdjacentHTML(
					'beforeend',
					'<div class="issuelinkbox"><p>' + type + ' / ' + decisionKnowledgeElement.summary + '</p>'
							+ '<p>Description: ' + decisionKnowledgeElement.description + '</p></div>');
			document.getElementById(simpleKnowledgeTypes[i]).style.display = "block";
		}
	}
}

function addOptionToAddDecisionComponent(type, id) {
	setUpEditorContentForAddingComponent(type);
	var submitButton = document.getElementById("form-input-submit" + type);
	submitButton.addEventListener("click", function() {
		var summary = document.getElementById("form-input-summary" + type).value;
		var description = "TODO";
		if (submitButton.id === "form-input-submitArgument") {
			type = $('input[name=form-radio-argument]:checked').val();
		}
		createDecisionKnowledgeElementAsChild(summary, description, type, id);
		document.getElementById("form-input-summary" + type).value = "";
	});
}

function setUpEditorContentForAddingComponent(type) {
	var radioButtons = "";
	if (type === "Argument") {
		radioButtons = "<input type='radio' class='radio' name='form-radio-argument' id='Pro' value='Pro Argument' checked='checked'>"
				+ "<label for='Pro'>Pro</label> "
				+ "<input type='radio' class='radio' name='form-radio-argument' id='Contra' value='Contra Argument'>"
				+ "<label for='Contra'>Contra</label> "
				+ "<input type='radio' class='radio' name='form-radio-argument' id='Comment' value='Comment'>"
				+ "<label for='Comment'>Comment</label> ";
	}
	document.getElementById(type).insertAdjacentHTML(
			'beforeend',
			'<p>Do you want to add an additional ' + type + '? ' + radioButtons
					+ '<input type="text" id="form-input-summary' + type + '" placeholder="Summary of ' + type
					+ '" class="text long-field"> <input type="submit" class="aui-button" id="form-input-submit'
					+ type + '" value="Add ' + type + '"/></p>');
}

function deleteContentOfAccordionEditor() {
	var details = document.getElementById("Details");
	clearInner(details);
	details.innerHTML = "";
	for (var index = 0; index < simpleKnowledgeTypes.length; index++) {
		document.getElementById(simpleKnowledgeTypes[index]).innerHTML = "";
		document.getElementById(simpleKnowledgeTypes[index]).style.display = "none";
	}
}