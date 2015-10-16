(function() {
  'use strict';

  /**
   * @ngdoc function
   * @name skrollApp.controller:SearchCtrl
   * @description
   * # SearchCtrl
   * Controller of the SearchCtrl
   */

  angular
    .module('app.search')
    .controller('SearchCtrl', SearchCtrl);

  /* @ngInject */
  function SearchCtrl($scope, selectionService, documentModel, searchFactory) {

    //-- private variables
    /*jshint validthis: true */
    var vm = this;
    vm.selectionService = selectionService;
    vm.searchState = $scope.searchState = searchFactory.searchState;
    vm.searchResults = searchFactory.searchResults;

    //-- public methods
    this.getMatches = getMatches;
    vm.getSurroundingText = getSurroundingText;
    vm.highlightSearchResults = highlightSearchResults;
    vm.unHighlightPreviousSearchResults = unHighlightPreviousSearchResults;
    vm.enterSearchBox = enterSearchBox;
    vm.leaveSearchBox = leaveSearchBox;
    vm.searchTextChange = searchTextChange;
    vm.selectedItemChange = selectedItemChange;

    //////////////////
    $scope.$watch('searchState.searchText', function(searchText) {
      vm.searchState.searchActive = searchText.length ? true : false;
      if (vm.searchState.searchActive) {
        vm.searchResults = vm.getMatches(searchText);
      }
    }, 250); // delay 250 ms

    function getMatches(searchText) {
      if (!documentModel.lunrIndex) {
        return;
      }
      var items = [];
      var searchResults = documentModel.lunrIndex
        .search(searchText);

      searchResults = searchResults.slice(0, 50);

      searchResults = searchResults.sort(function(a, b) {
        return parseInt(a.ref.split('_')[1]) - parseInt(b.ref.split('_')[1]);
      });

      var elementsOrganizedInHeaders = {};
      var headerItems = LHSModel.getParaFromClassIdRange(2, 4);
      var currHeader = headerItems[0];
      var nextHeaderIdx = 0,
        currHeaderIdx = 0;
      nextHeaderIdx++;
      var searchResultsIdx = 0;
      var nextHeader = headerItems[nextHeaderIdx];
      while (searchResultsIdx < searchResults.length && nextHeaderIdx < headerItems.length) {
        var result = searchResults[searchResultsIdx];
        var resultParaId = parseInt(result.ref.split('_')[1]);
        if (resultParaId > parseInt(currHeader.paragraphId.split('_')[1])) {
          if (resultParaId < parseInt(nextHeader.paragraphId.split('_')[1])) {
            if (typeof elementsOrganizedInHeaders[currHeaderIdx] === 'undefined') {
              elementsOrganizedInHeaders[currHeaderIdx] = [];
            }
            elementsOrganizedInHeaders[currHeaderIdx].push(result);
            searchResultsIdx++;
          } else {
            currHeader = nextHeader;
            currHeaderIdx = nextHeaderIdx;
            nextHeaderIdx++;
            nextHeader = headerItems[nextHeaderIdx];
          }
        } else if (resultParaId === parseInt(currHeader.paragraphId.split('_')[1])) {
          if (typeof elementsOrganizedInHeaders[currHeaderIdx] === 'undefined') {
            elementsOrganizedInHeaders[currHeaderIdx] = [];
          }
          result.para
          elementsOrganizedInHeaders[currHeaderIdx].push(result);
          searchResultsIdx++;
        } else if (currHeaderIdx === 0) {
          if (typeof elementsOrganizedInHeaders[-1] === 'undefined') {
            elementsOrganizedInHeaders[-1] = [];
          }
          elementsOrganizedInHeaders[-1].push(result);
          searchResultsIdx++;
        } else {
          currHeader = nextHeader;
          currHeaderIdx = nextHeaderIdx;
          nextHeaderIdx++;
          nextHeader = headerItems[nextHeaderIdx];
        }
      }

      while (searchResultsIdx < searchResults.length) {
        var result = searchResults[searchResultsIdx];
        if (typeof elementsOrganizedInHeaders[currHeaderIdx] === 'undefined') {
          elementsOrganizedInHeaders[currHeaderIdx] = [];
        }
        elementsOrganizedInHeaders[currHeaderIdx].push(result);
        searchResultsIdx++;
      }


      var self = this;
      var ii = 0;
      for (var idx in elementsOrganizedInHeaders) {
        var header = headerItems[idx];
        var results = elementsOrganizedInHeaders[idx];

        // results.sort(function(a, b) {
        //   return b.score - a.score;
        // });
        var searchedItems = [];

        results.forEach(function(result) {
          if (result.ref === header.paragraphId) {
            return;
          }
          var resultElement = document.getElementById(result.ref);
          var resultText = self.getSurroundingText(resultElement.textContent, searchText);
          var item = {
            //'header': idx === "-1" ? "DocumentStart" : header.term,
            'paragraphId': result.ref,
            'displayText': resultText
          };
          searchedItems.push(item);
        });

        var headerObj = {
          'headerTerm': idx === "-1" ? '' : header.term,
          'paragraphId': header.paragraphId,
          'isHidden': idx === "-1",
          'searchedItems': searchedItems
        };
        if (idx === "-1") {
          items.unshift(headerObj);
        } else {
          items.push(headerObj);
        }
      }

      //vm.unHighlightPreviousSearchResults(vm.searchResults);
      //vm.highlightSearchResults(items, searchText);
      // vm.searchResults = items;
      // vm.searchText = searchText;
      // items = _.groupBy(items, function(item) {
      //   return item.header
      // });

      return items;
    }

    function getSurroundingText(paragraphText, searchString) {
      var possibleWordsBefore = 2;
      var possibleWordsAfter = 16;
      var regexStr = "((?:[\\w]*\\s*){" + possibleWordsBefore + "}" + searchString + "(?:\\s*[\\w,-\\.]*){" + possibleWordsAfter + "})";
      var regex = new RegExp(regexStr, 'i');
      var matcher;
      if ((matcher = regex.exec(paragraphText)) !== null) {
        if (matcher.index === regex.lastIndex) {
          regex.lastIndex++;
        }
        return matcher[0];
      }
      return paragraphText;
      // @todo: once we have stem information from lunr,
      // then we can stop returning paragraph as it is whenever
      // we are not able to find surrounding text.
      //return '';
    }

    function highlightSearchResults(items, searchText) {
      for (var ii = 0; ii < items.length; ii++) {
        var paraId = items[ii].paragraphId;
        $("#" + paraId).highlight(searchText, {
          wordsOnly: false,
          element: 'span',
          className: 'skHighlight'
        });
      }
    }

    function unHighlightPreviousSearchResults(items) {
      for (var ii = 0; ii < items.length; ii++) {
        var paraId = items[ii].paragraphId;
        $("#" + paraId).unhighlight({
          element: 'span',
          className: 'skHighlight'
        });
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
    // jQuery.expr[":"].ContainsCaseInsensitive = jQuery.expr.createPseudo(function(arg) {
    //   return function(elem) {
    //     return jQuery(elem).text().toUpperCase().indexOf(arg.toUpperCase()) >= 0;
    //   };
    // });
  }
})();