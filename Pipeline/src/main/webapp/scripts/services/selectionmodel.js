'use strict';

/**
 * @ngdoc service
 * @name SkrollApp.SelectionModel
 * @description
 * # SelectionModel
 * Factory in the SkrollApp.
 */

var SelectionModel = {
	paragraphId: '',
	selectedText: '',
	mouseDownParaId: '',
	serializedSelection: '',
	serializedParagraphId: '',

	scrollToParagraph: function(paragraphId) {
	  var para = $("#" + paragraphId);
	  $("#" + SelectionModel.paragraphId).css("background-color", "");
	  if (para != null) {
	    var contentDiv = $("#skrollport");
	    $("#skrollport").animate({
	      scrollTop: ($("#skrollport").scrollTop() - 200 + $(
	        para).offset().top)
	    }, "slow");
	    $(para).css("background-color", "yellow");
	    SelectionModel.paragraphId = paragraphId;
	  }
	},

	scrollToSelection: function(selectionId) {
	  var savedSelection = JSON.parse(selectionId);
	  var rangySelection  = savedSelection.rangy;
	  var paraId = savedSelection.paraId;
	  rangy.getSelection().restoreCharacterRanges($("#"+paraId).get(0), rangySelection);
	  var selection = rangy.getSelection();
	  var dom = selection.anchorNode;
	  var para = $(dom);
	  //assuming that node is a text node
	  if (para != null) {
	    var contentDiv = $("#skrollport");
	    var paraOffset = 0;
	    if ($(para).get(0).nodeType == 3) {
	    	//use parent
	    	paraOffset = $(para).parent().offset().top;
	    } else {
	    	paraOffset = $(para).offset().top ;
	    }
	    $("#skrollport").animate({
	      scrollTop: ($("#skrollport").scrollTop() - 200 + paraOffset)
	    }, "slow");
	  }
	},

	saveSelection: function(paraId, selectedText) {
		this.paragraphId = paraId;
		this.selectedText = selectedText;
		this.serializedParagraphId = paraId;
		var selection = { };
		selection.rangy = rangy.getSelection().saveCharacterRanges($("#"+paraId).get(0));
		selection.paraId = paraId;
		this.serializedSelection = JSON.stringify(selection);
		this.serializedSelection = encodeURIComponent(encodeURIComponent(this.serializedSelection));
   	 	this.$ngSilentLocation.silent('/view/docId/'+documentModel.documentId+'/linkId/'+this.serializedSelection, false);
	},

	clearSelection: function() {
		//clear highlight
		if (this.paragraphId != '') {
			this.removeHighlightParagraph(this.paragraphId);
		}
		this.paragraphId = '';
		this.selectedText = '';
		this.serializedSelection = "";
		this.serializedParagraphId = "";

	},

	removeHighlightParagraph: function(paraId) {
	  $("#" + paraId).css("background-color", "");
	}

};


// angular.module('SkrollApp')
// 	.factory('SelectionModel', function($location, $ngSilentLocation) {
// 		SelectionModel.$location = $location;
// 		SelectionModel.$ngSilentLocation = $ngSilentLocation;
// 		return SelectionModel;
// 	});