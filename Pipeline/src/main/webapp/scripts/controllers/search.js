'use strict';

/**
 * @ngdoc function
 * @name skrollApp.controller:SearchCtrl
 * @description
 * # SearchCtrl
 * Controller of the SearchCtrl
 */

var SearchCtrl = function() {

	//-- private variables
	this.searchText;

}

SearchCtrl.prototype.getMatches = function(searchText) {
	var items = [];
	var elements = $("[id^='p_']:contains('" + searchText + "')");
	//convert level terms to integers
	var levelsPara = [];
	for (var ii = 0; ii < LHSModel.smodel.terms.length; ii++) {
		var str = LHSModel.smodel.terms[ii].paragraphId.split("_")[1];
		var paraId = parseInt(str);
		levelsPara.push(paraId);
	}
	//iterate over each search result
	$.each(elements, function(i, val) {
		var id = parseInt($(val).attr('id').split("_")[1]);
		var header = 0;
		var displayText;
		var text = $('#p_' + id).text();
		var indexOfSearch = text.indexOf(searchText);
		if (indexOfSearch > 40) {
			displayText = text.substring(0, 39) + '.....' + searchText + '....';
		} else {
			displayText = text.substring(0, indexOfSearch) + text.substring(indexOfSearch + 1, searchText.length) + '...';
		}
		// for (var jj = 1; jj < levelsPara.length; jj++) {
		// 	if (id > levelsPara[jj]) {
		// 		if (jj > 1) {
		// 			header = LHSModel.smodel.terms[jj - 1].term;
		// 			var paragraphId = 'p_' + levelsPara[jj];
		// 			var text = $('#p_' + id).text();
		// 			var indexOfSearch = text.indexOf(searchText);
		// 			if (indexOfSearch > 40) {
		// 				displayText = text.substring(0, 39) + '.....' + searchText + '....';
		// 			} else {
		// 				displayText = text.substring(0, indexOfSearch) + text.substring(indexOfSearch + 1, searchText.length) + '...';
		// 			}
		// 			//check for first 15 characters
		// 		}
		// 	}
			var item = new Object();
			item['display'] = header;
			item['paragraphId'] = 'p_' + id;
			item['displayText'] = displayText;
			items.push(item);
		//}
	})
	console.log(items);
	return items;
}

SearchCtrl.prototype.searchTextChange = function(text) {
  console.log('Text changed to ' + text);
}

SearchCtrl.prototype.selectedItemChange = function(item) {
  console.log('Item changed to ' + JSON.stringify(item));
  var paragraphId = item.paragraphId;
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

angular.module('SkrollApp')
	.controller('SearchCtrl', SearchCtrl);
