(function() {
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

	6. Changed MAX_HEIGHT from 5 to 8 in autocompleteController.js

	7. Uncommented a line in focus() method to show search results when the widget
	   gains focus
	*/

	angular
		.module('app.search')
		.controller('SearchCtrl', SearchCtrl);

	/** @ngInject **/
	function SearchCtrl(selectionService, documentModel) {

		//-- private variables
		/*jshint validthis: true */
		var vm = this;
		vm.searchText = '';
		vm.selectionService = selectionService;
		vm.searchResults = [];


		//-- public methods
		this.getMatches 							= getMatches;
		vm.getSurroundingText				= getSurroundingText;
		vm.highlightSearchResults 	= highlightSearchResults;
		vm.unHighlightPreviousSearchResults = unHighlightPreviousSearchResults;
		vm.enterSearchBox						= enterSearchBox;
		vm.leaveSearchBox						= leaveSearchBox;
		vm.searchTextChange 				= searchTextChange;
		vm.selectedItemChange 			= selectedItemChange;


    //////////////

		function getMatches(searchText) {
			var items = [];
			var elements = documentModel.lunrIndex.search(searchText).map(function(searchObj) {
				console.log(searchObj.ref);
				return document.getElementById(searchObj.ref);
			});
			//var elements = $(":ContainsCaseInsensitive('" + searchText + "')").filter(":not(:has(*))").closest("[id^='p_']");
			//var elements = $("[id^='p_']:not('[id^=\\'p_\\']')").filter(":contains('" + searchText + "')");
			//var elements = $("[id^='p_']:only-child").filter(":contains('" + searchText + "')");
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
				//var surroundingStartIndex = 80;
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
				if (items.length > 15) {
					return false;
				}
			})
			var t1 = new Date().getTime();
			this.unHighlightPreviousSearchResults(this.searchResults);
			console.log((new Date().getTime()) - t1);
			t1 = new Date().getTime();
			this.highlightSearchResults(items, searchText);
			console.log((new Date().getTime()) - t1);
			this.searchResults = items;
			this.searchText = searchText;
			return items;
		}

		function getSurroundingText(paragraphText, searchString) {
      var possibleWordsBefore = 4;
      var possibleWordsAfter = 32;
      var regexStr = "((?:[\\w]*\\s*){"+possibleWordsBefore+"}"+searchString+"(?:\\s*[\\w,-\\.]*){"+possibleWordsAfter+"})";
      var regex = new RegExp(regexStr, 'i');
      var matcher;
      if ((matcher = regex.exec(paragraphText)) !== null) {
          if (matcher.index === regex.lastIndex) {
              regex.lastIndex++;
          }
          return matcher[0];
      }
      return '';
		}

		function highlightSearchResults(items, searchText) {
			for(var ii = 0; ii < items.length; ii++) {
				var paraId = items[ii].paragraphId;
				$("#"+paraId).highlight(searchText, { wordsOnly: true, element: 'span', className: 'skHighlight' });
			}
		}

		function unHighlightPreviousSearchResults(items) {
			for(var ii = 0; ii < items.length; ii++) {
				var paraId = items[ii].paragraphId;
				$("#"+paraId).unhighlight({ element: 'span', className: 'skHighlight' });
			}
		}


		function enterSearchBox() {
		  LHSModel.smodel.hover = true;
		}

		function leaveSearchBox() {
		  LHSModel.smodel.hover = false;
		}



		function searchTextChange(text) {
		  if (text == '') {
		  	console.log("clearing");
		  	this.unHighlightPreviousSearchResults(this.searchResults);
		  }
		}

		function selectedItemChange(item) {
			if (item == null) {
				return;
			}
		  var paragraphId = item.paragraphId;
		  this.selectionService.scrollToParagraph(paragraphId);
		}


		jQuery.expr[":"].ContainsCaseInsensitive = jQuery.expr.createPseudo(function(arg) {
		   return function( elem ) {
		   	return jQuery(elem).text().toUpperCase().indexOf(arg.toUpperCase()) >= 0;
		   };
		});
	}

})();
