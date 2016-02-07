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
  function SearchCtrl($scope, $timeout, selectionService, documentModel, searchFactory, $analytics) {

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
    // vm.enterSearchBox = enterSearchBox;
    // vm.leaveSearchBox = leaveSearchBox;
    vm.searchTextChange = searchTextChange;
    vm.selectedItemChange = selectedItemChange;
    vm.onKeyDown = onKeyDown;
    vm.onKeyUp = onKeyUp;
    vm.onClick = onClick;

    vm.typingTimer;
    vm.doneTypingInterval = 50;
    vm.doneTyping = doneTyping;

    vm.settledSearchTimer;
    vm.settledSearchInterval = 2000;
    vm.settledSearch = settledSearch;

    //////////////////
    // $scope.$watch('searchState.searchText', _.debounce(function(searchText) {
    //   $scope.$apply(function() {
    //     vm.searchState.searchActive = searchText.length ? true : false;
    //     if (vm.searchState.searchActive) {
    //       vm.searchResults = vm.getMatches(searchText);
    //     }
    //   });
    // }, 50));

    function onKeyDown(event) {
      if (event.keyCode === 40 || event.keyCode === 38) {
        event.preventDefault();
        event.stopPropagation();
      } else {
      //clearTimeout(vm.typingTimer);
        $timeout.cancel(vm.typingTimer);
        $timeout.cancel(vm.settledSearchTimer);
      }

      if (event.keyCode === 40 || event.keyCode === 38) {
        var prevSelectedIndex = vm.searchState.currSelectedIndex;
        var lhsContent = $('sk-search').parent();
        vm.searchListItems = $('sk-search .highlight').closest('.search-result-container');
        if (event.keyCode === 40) { //DownArrow
          vm.searchState.currSelectedIndex = Math.min(vm.searchState.currSelectedIndex + 1, vm.searchListItems.length - 1);
        } else if (event.keyCode === 38) { //UpArrow
          vm.searchState.currSelectedIndex = Math.max(vm.searchState.currSelectedIndex - 1, 0);
        }
        var index = vm.searchState.currSelectedIndex;
        vm.searchListItems.eq(prevSelectedIndex).toggleClass('selected-list-item', false);
        var listItem = vm.searchListItems.eq(index);
        listItem.toggleClass('selected-list-item', true);

        if (event.keyCode === 40) { //DownArrow
          lhsContent.stop(true, true).animate({
            scrollTop: (function() {
              return lhsContent.scrollTop() + Math.max(0, listItem.outerHeight() - (lhsContent.outerHeight() - listItem.offset().top) - lhsContent.parent().find('md-toolbar').outerHeight());
            })()
          }, 'fast');
        } else if (event.keyCode === 38) { //UpArrow
          lhsContent.stop(true, true).animate({
            scrollTop: (function() {
              return lhsContent.scrollTop() + Math.min(listItem.offset().top - lhsContent.parent().find('md-toolbar').outerHeight(), 0);
            })()
          }, 'fast');
        }
        if(listItem.attr('scroll-to-paragraph')) {
          selectionService.scrollToParagraph(listItem.attr('scroll-to-paragraph'), vm.searchState.searchText);
        } else {
          selectionService.scrollToParagraph(listItem.find('[scroll-to-paragraph]').attr('scroll-to-paragraph'), vm.searchState.searchText);
        }
      }
    }

    //user is "finished typing,"
    function doneTyping() {
      var searchText = vm.searchState.searchText;
      vm.searchState.searchActive = searchText.length ? true : false;
      if (vm.searchState.searchActive) {
        vm.searchResults = vm.getMatches(searchText);
      }
    }

    //user is "finished typing," for seme time.
    function settledSearch() {
      var searchText = vm.searchState.searchText;
      console.log('Settled on search after '+vm.settledSearchInterval+' ms:' + searchText);
      $analytics.eventTrack(documentModel.documentId, {
              category: 'searchText',
              label: searchText
            });
    }

    function onKeyUp(event, searchText) {
      if (event.keyCode === 40 || event.keyCode === 38) {
        event.preventDefault();
        event.stopPropagation();
        return;
      }
      if (searchText === '') {
        return;
      }
      vm.searchState.currSelectedIndex = -1;
      vm.searchState.searchText = searchText;
      //clearTimeout(vm.typingTimer);
      $timeout.cancel(vm.typingTimer);
      vm.typingTimer = $timeout(vm.doneTyping, vm.doneTypingInterval);
      $timeout.cancel(vm.settledSearchTimer)
      vm.settledSearchTimer = $timeout(vm.settledSearch, vm.settledSearchInterval);
    }

    function onClick(event) {
      if (typeof event === 'undefined') {
        return;
      }
      vm.searchListItems = $('sk-search .highlight').closest('.search-result-container');
      var lhsContent = $('sk-search').parent();
      var listItem = $(event.target).closest('.search-result-container');
      if($(event.target).hasClass('highlight') || $(event.target).find('.highlight').length != 0) {
        listItem.toggleClass('selected-list-item', true);
      }
      var prevSelectedIndex = vm.searchState.currSelectedIndex;
      vm.searchListItems.eq(prevSelectedIndex).toggleClass('selected-list-item', false);
      var index = vm.searchState.currSelectedIndex = vm.searchListItems.index(listItem);

      if (((listItem.offset().top - lhsContent.parent().find('md-toolbar').outerHeight())) < 0) {
        lhsContent.stop(true, true).animate({
          scrollTop: (function() {
            return lhsContent.scrollTop() + Math.min(listItem.offset().top - lhsContent.parent().find('md-toolbar').outerHeight(), 0);
          })()
        }, 'fast');
      } else if (listItem.outerHeight() - (lhsContent.outerHeight() - listItem.offset().top) - lhsContent.parent().find('md-toolbar').outerHeight() > 0) {
        lhsContent.stop(true, true).animate({
          scrollTop: (function() {
            return lhsContent.scrollTop() + Math.max(0, listItem.outerHeight() - (lhsContent.outerHeight() - listItem.offset().top) - lhsContent.parent().find('md-toolbar').outerHeight());
          })()
        }, 'fast');
      }

    }

    function getMatches(searchText) {
      if (!documentModel.lunrIndex) {
        return;
      }
      var items = [];
      var searchResults = documentModel.lunrIndex
        .search(searchText);

      searchResults = searchResults.slice(0, 100);

      searchResults = searchResults.sort(function(a, b) {
        return parseInt(a.ref.split('_')[1]) - parseInt(b.ref.split('_')[1]);
      });

      var elementsOrganizedInHeaders = {};
      var headerItems = LHSModel.getParaFromClassIdRange(2, 3);
      //check if there are no header items and create a fake header
      if (headerItems.length == 0) {
        //create a fake header
        headerItems.push({'term' : '', paragraphId: 'p_1236'});
      }
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

        var searchedItems = [];

        results.forEach(function(result) {
          if (typeof header !== 'undefined' && result.ref === header.paragraphId) {
            return;
          }
          // var resultElement = document.getElementById(result.ref);
          var resultElement = selectionService.getJQParaElement(result.ref).get(0);
          var resultText;
          var resultType = 0;
          //different text for table
          if (($('> table', resultElement).length) > 0) {
            resultText = 'Table';
            resultType = 1;
          } else {
            resultText = self.getSurroundingText(resultElement.textContent, searchText);
          }

          // var resultText = self.getSurroundingText(resultElement.textContent, searchText);
          var item = {
            'paragraphId': result.ref,
            'displayText': resultText,
            'resultType': resultType
          };
          searchedItems.push(item);
        });

        var headerObj = {
          'headerTerm': idx === '-1' ? '' : header.term,
          'paragraphId': idx === '-1' ? '' : header.paragraphId,
          'isHidden': idx === '-1',
          'searchedItems': searchedItems
        };
        if (idx === '-1') {
          items.unshift(headerObj);
        } else {
          items.push(headerObj);
        }
      }

      return items;
    }

    function getSurroundingText(paragraphText, searchString) {
      var possibleWordsBefore = 2;
      var possibleWordsAfter = 24;
      var regexStr = '((?:[\\w]*\\s*){' + possibleWordsBefore + '}' +
        searchString + '(?:\\s*[\\w,-\\.]*){' + possibleWordsAfter + '})';
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
        $('#' + paraId).highlight(searchText, {
          wordsOnly: false,
          element: 'span',
          className: 'skHighlight'
        });
      }
    }

    function unHighlightPreviousSearchResults(items) {
      for (var ii = 0; ii < items.length; ii++) {
        var paraId = items[ii].paragraphId;
        $('#' + paraId).unhighlight({
          element: 'span',
          className: 'skHighlight'
        });
      }
    }

    //@todo remove these
    // function enterSearchBox() {
    //   LHSModel.smodel.hover = true;
    // }

    // function leaveSearchBox() {
    //   LHSModel.smodel.hover = false;
    // }

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
  }
})();