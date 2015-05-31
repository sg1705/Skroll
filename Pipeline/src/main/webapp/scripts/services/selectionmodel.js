'use strict';

/**
 * @ngdoc service
 * @name SkrollApp.SelectionModel
 * @description
 * # SelectionModel
 * Factory in the SkrollApp.
 */

var SelectionModel = {
	paragraphId: "",
	selectedText: "",
	mouseDownParaId: "",

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
	}
};


angular.module('SkrollApp')
	.factory('SelectionModel', function() {
		return SelectionModel;
	});