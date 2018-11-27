/*
 This tab panel view does:
 * toggle sentence types
 * show a tree of relevant decision knowledge

 Requires
 * condec.api.js
 * condec.observable.js
 
 Is required by
 * tabPanel.vm 
 
 Is referenced in HTML by
 * tabPanel.vm 
 */
(function(global) {
	/* private vars */
	var contextMenu = null;
	var conDecAPI = null;
	var conDecObservable = null;
	var treeViewer = null;
	var i18n = null;

	var ConDecIssueTab = function ConDecIssueTab() {
	};

	ConDecIssueTab.prototype.init = function init(_conDecAPI, _conDecObservable, _treeViewer, _contextMenu, _i18n) {
		console.log("view.tab.panel.js init");

		// TODO add simple type checks
		conDecAPI = _conDecAPI;
		conDecObservable = _conDecObservable;
		treeViewer = _treeViewer;
		contextMenu = _contextMenu;
		i18n = _i18n;

		// Register/subscribe this view as an observer
		conDecObservable.subscribe(this);

		return true;
	};

	/* triggered by onchange event in tabPanel.vm */
	function toggleSelectedDecisionElements() {
		console.log("view.tab.panel.js toggleSelectedDecisionElements");

		var decisionElements = [ "Issue", "Decision", "Alternative", "Argument", "Relevant" ];
		var checked = [];

		for (var i = 0; i < decisionElements.length; i++) {
			var check = document.getElementById(decisionElements[i]).checked;
			checked.push(check);
		}
		return checked;
	}

	/*
	 called by
	 * view.tab.panel.js:callDialog
	 * view.context.menu.js
	 */
	function buildTreeViewer(knowledgeTypeSelection) {
		console.log("view.tab.panel.js buildTreeViewer");

		conDecAPI.getTreeViewerWithoutRootElement(knowledgeTypeSelection, function(core) {
			console.log("view.tab.panel.js getTreeViewerWithoutRootElement callback");

			jQueryConDec("#jstree").jstree({
				"core" : core,
				"plugins" : [ "dnd", "wholerow", "search", "sort", "state" ],
				"search" : {
					"show_only_matches" : true
				},
				"sort" : sortfunction
			});
			$("#jstree-search-input").keyup(function() {
				var searchString = $(this).val();
				jQueryConDec("#jstree").jstree(true).search(searchString);
			});
			console.log(treeViewer)
			treeViewer.addDragAndDropSupportForTreeViewer();
			treeViewer.addContextMenuToTreeViewer();
		});
	}

	/* used by buildTreeViewer */
	function sortfunction(a, b) {
		a1 = this.get_node(a);
		b1 = this.get_node(b);
		if (a1.id > b1.id) {
			return 1;
		} else {
			return -1;
		}
	}

	function updateView() {
		console.log("view.tabPanel updateView");
		treeViewer.resetTreeViewer();
		var checked = toggleSelectedDecisionElements();
		buildTreeViewer(checked);
	}

	ConDecIssueTab.prototype.fetchAndRender = function() {
		buildTreeViewer([ true, true, true, true, true ]);
	};

	// Expose methods:
	ConDecIssueTab.prototype.updateView = updateView;
	// for tabPanel.vm
	ConDecIssueTab.prototype.toggleSelectedDecisionElements = toggleSelectedDecisionElements;
	// for view.context.menu.js
	ConDecIssueTab.prototype.buildTreeViewer = buildTreeViewer;

	// export ConDecIssueTab
	global.conDecIssueTab = new ConDecIssueTab();
})(window);