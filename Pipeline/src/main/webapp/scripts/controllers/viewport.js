'use strict';

/**
 * @ngdoc function
 * @name skrollApp.controller:ViewPortCtrl
 * @description
 * # ViewPortCtrl
 * Controller of the ViewPortCtrl
 */

var ViewPortCtrl = function (SelectionModel, documentService) {
	this.SelectionModel = SelectionModel;
	this.documentService = documentService;
}

ViewPortCtrl.prototype.paraClicked = function($event) {
	//clear highlight
	if (this.SelectionModel.paragraphId != '') {
		this.removeHighlightParagraph(this.SelectionModel.paragraphId);
	}


    var paraId = this.inferParagraphId($event);

    if (paraId == null)
    	return;

    //store in SelectionModel
    this.SelectionModel.paragraphId = paraId;
    //highlight paragraph
    this.highlightParagraph(paraId);

    this.documentService
    .getParagraphJson(paraId)
    .then(function(data) {
        $("#rightPane").html(JSON.stringify(data, null, 2));
    },function(data, status) {
        console.log(status);
    });

}

ViewPortCtrl.prototype.highlightParagraph = function(paraId) {
	$("#"+paraId).css("background-color","yellow");
}

ViewPortCtrl.prototype.removeHighlightParagraph = function(paraId) {
	$("#"+paraId).css("background-color","");
}


ViewPortCtrl.prototype.inferParagraphId = function($event) {

    var parents = $($event.target).parents("div[id^='p_']");
    for(var ii = 0; ii < parents.length; ii++ ) {
        console.log($(parents[ii]).attr('id'));
    }

    if (parents.length > 1) {
    	return $(parents[0]).attr('id');
    } else {
    	return null;
    }

}


angular.module('SkrollApp')
	.controller('ViewPortCtrl', ['SelectionModel', 'documentService', ViewPortCtrl]);
