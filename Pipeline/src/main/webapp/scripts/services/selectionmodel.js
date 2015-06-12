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
	    ToolbarModel.trainerToolbar.lastJson = '';
	  }
	},

	scrollToSelection: function(selectionId) {
	  var selection = rangy.deserializeSelection(selectionId, $("#content").get(0));
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
	    ToolbarModel.trainerToolbar.lastJson = '';
	  }
	},

	saveSelection: function(paraId, selectedText) {
		this.paragraphId = paraId;
		this.selectedText = selectedText;
		this.serializedSelection = rangy.serializeSelection(rangy.getSelection($("#content").get(0)), false, $("#content").get(0));
		this.serializedSelection = encodeURIComponent(encodeURIComponent(this.serializedSelection));
		this.serializedParagraphId = paraId;
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


angular.module('SkrollApp')
	.factory('SelectionModel', function($location, $ngSilentLocation) {
		SelectionModel.$location = $location;
		SelectionModel.$ngSilentLocation = $ngSilentLocation;
		return SelectionModel;
	});