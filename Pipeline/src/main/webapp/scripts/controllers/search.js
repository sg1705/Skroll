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

There are four hacks in angularjs-material

a) handleQuery() method sets a max-width to 270px
b) updateScroll() add an action to scroll to paragraph
c) angular-material.css; commented height: 450px
d) angular-material.js changed the height section.. commented some assumptions about height
c) angular-material.js .. added <md-input-container> to md-autocomplete directive
   with default-dark theme

3. start.html points to non-minified versions of angular-material

4. MdAutocomplete added a width of 270px in the template

5. MdAutocompleteCtrl changed the MAX_HEIGHT from 5.5 to 11
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
	//var headerItems = LHSModel.getTermsForClass(2);
	var headerItems = LHSModel.getParaFromClassIdRange(2,4);
	var levelsPara = [];
	for (var ii = 0; ii < headerItems.length; ii++) {
		var str = headerItems[ii].paragraphId.split("_")[1];
		var paraId = parseInt(str);
		levelsPara.push(paraId);
	}
	var self = this;
	//iterate over each search result
	$.each(elements, function(i, val) {
		var id = parseInt($(val).attr('id').split("_")[1]);
		var header;
		var displayText;
		var isItem = false;
		var surroundingStartIndex = 80;
		//this is a hack
		if (id > 1237) {
			//iterate over each level 2 heading
			for (var jj = 1; jj < levelsPara.length; jj++) {
				//check if this heading is the heading for this paragraph
				if (levelsPara[jj] > id) {
					if (jj > 1) {
						header = headerItems[jj - 1].term;
						var text = $('#p_' + id).text();
						displayText = self.getSurroundingText(text, searchText);
						// var indexOfSearch = text.indexOf(searchText);
						// if (indexOfSearch > surroundingStartIndex) {
						// 	displayText = text.substring(0, surroundingStartIndex - 1) + '..... ' + searchText + ' .... ';
						// } else {
						// 	displayText = text.substring(0, indexOfSearch) + text.substring(indexOfSearch + 1, searchText.length) + '...';
						// }
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
		if ((searchText.length <=2) && (items.length > 10)) {
			return false;
		}

	})
	return items;
}

SearchCtrl.prototype.getSurroundingText = function(paragraphText, searchString) {
	var indexOfSearch = paragraphText.indexOf(searchString);
	var lengthOfSearch = searchString.length;
	var expandLeft = 50;
	var expandRight = 50;
	var startLeft = 0;
	var endRight = 0;
	var length = paragraphText.length;
	if (indexOfSearch < expandLeft) {
		startLeft = 0;
	} else {
		startLeft = indexOfSearch - expandLeft
	}

	if ( (length - (indexOfSearch + lengthOfSearch)) < expandRight) {
		endRight = length -1;
	} else {
		endRight = (indexOfSearch + lengthOfSearch + expandRight);
	}
	console.log(indexOfSearch + ":"+ endRight);
	var text = paragraphText.substr(startLeft, endRight - startLeft);
	return text;
}

SearchCtrl.prototype.enterSearchBox = function() {
  LHSModel.smodel.hover = true;
}

SearchCtrl.prototype.leaveSearchBox = function() {
  LHSModel.smodel.hover = false;
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
