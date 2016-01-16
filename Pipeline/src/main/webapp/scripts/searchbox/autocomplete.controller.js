(function () {
  'use strict';


  angular
      .module('app.searchbox')
      .controller('AutoCompleteCtrl', AutoCompleteCtrl);

  function AutoCompleteCtrl ($timeout, $q, searchBoxModel, searchBoxService) {
    var vm = this;

    vm.readonly = false;
    vm.selectedItem = null;
    vm.searchState = searchBoxModel.searchState;
    vm.querySearch = querySearch;
    vm.dataElements = loadData();
    vm.selectedChips = searchBoxModel.searchState.selectedChips;
    vm.numberChips = [];
    vm.numberChips2 = [];
    vm.numberBuffer = '';
    vm.autocompleteDemoRequireMatch = false;
    vm.transformChip = transformChip;
    //vm.searchText = vm.searchState.searchText;

    /**
     * Return the proper object when the append is called.
     */
    function transformChip(chip) {
      // If it is an object, it's already a known chip
      if (angular.isObject(chip)) {
        return chip;
      }

      // Otherwise, create a new one
      return { name: chip, type: 'new' }
    }

    /**
     * Search for sec filing.
     */
    function querySearch (query) {
      var self = this;
      var results = [];
      var k = 0;
      return searchBoxService.getSuggestions(query).then(function(terms) {
        var groups = terms.grouped.type.groups;
        for (var i in groups) {
          console.log(groups[i]);
          var docs = groups[i].doclist.docs;
          results.push.apply(results, docs);
        }
          console.log(results);
          return results;
      }, function(data, status) {
        console.log(status);
      });
     
      //var results = query ? vm.dataElements.filter(createFilterFor(query)) : [];
      
    }

    /**
     * Create filter function for a query string
     */
    function createFilterFor(query) {
      var lowercaseQuery = angular.lowercase(query);

      return function filterFn(element) {
        return (element._lowername.indexOf(lowercaseQuery) === 0) ||
            (element._lowertype.indexOf(lowercaseQuery) === 0);
      };

    }


    function loadData() {
      var elements = [
        {
          'name': 'google',
          'type': 'company'
        },
        {
          'name': '10-K',
          'type': 'filing'
        },
        {
          'name': 'ex-33.1',
          'type': 'exbihit'
        },
        {
          'name': '2015',
          'type': 'year'
        },
        {
          'name': 'goog',
          'type': 'ticker'
        }
      ];

      return elements.map(function (element) {
        element._lowername = element.name.toLowerCase();
        element._lowertype = element.type.toLowerCase();
        return element;
      });
    }
  }
})();
