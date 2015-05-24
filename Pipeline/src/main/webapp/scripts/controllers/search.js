'use strict';

/**
 * @ngdoc function
 * @name skrollApp.controller:SearchCtrl
 * @description
 * # SearchCtrl
 * Controller of the SearchCtrl
 */

/*
TODO hacks for search
1. angular-material.css
Commented line-height and height for the .md-autocomplete-suggestions style

2. angular-material.js
Added scrolling in updateScroll method in MdAutocompleteCtrl 

3. start.html points to non-minified versions of angular-material

4. MdAutocomplete added a width of 270px in the template

*/

var SearchCtrl = function(SelectionModel) {

	//-- private variables
	this.searchText;
	this.SelectionModel = SelectionModel;
}

SearchCtrl.prototype.getMatches = function(searchText) {
	var items = [];
	var elements = $("[id^='p_']:contains('" + searchText + "')");
	//convert level terms to integers
	var headerItems = LHSModel.getTermsForClass(2);
	var levelsPara = [];
	for (var ii = 0; ii < headerItems.length; ii++) {
		var str = headerItems[ii].paragraphId.split("_")[1];
		var paraId = parseInt(str);
		levelsPara.push(paraId);
	}
	//iterate over each search result
	$.each(elements, function(i, val) {
		var id = parseInt($(val).attr('id').split("_")[1]);
		var header;
		var displayText;
		var isItem = false;
		//this is a hack
		if (id > 1237) {
			//iterate over each level 2 heading
			for (var jj = 1; jj < levelsPara.length; jj++) {
				//check if this heading is the heading for this paragraph
				if (levelsPara[jj] > id) {
					if (jj > 1) {
						header = headerItems[jj - 1].term;
						var text = $('#p_' + id).text();
						var indexOfSearch = text.indexOf(searchText);
						if (indexOfSearch > 40) {
							displayText = text.substring(0, 39) + '..... ' + searchText + ' .... ';
						} else {
							displayText = text.substring(0, indexOfSearch) + text.substring(indexOfSearch + 1, searchText.length) + '...';
						}
						isItem = true;
						break;
					}
				}
			}
			if (isItem) {
				var item = new Object();
				item['header'] = header;
				item['paragraphId'] = 'p_' + id;
				item['displayText'] = displayText;
				items.push(item);				
			}
		}
	})
	return items;
}

SearchCtrl.prototype.searchTextChange = function(text) {
  
}

SearchCtrl.prototype.selectedItemChange = function(item) {
	if (item == null) {
		return;
	}
  var paragraphId = item.paragraphId;
  this.SelectionModel.scrollToParagraph(paragraphId);
}

angular.module('SkrollApp')
	.controller('SearchCtrl', ['SelectionModel', SearchCtrl]);
