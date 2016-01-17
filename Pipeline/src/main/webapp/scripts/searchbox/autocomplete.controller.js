(function () {
  'use strict';


  angular
      .module('app.searchbox')
      .controller('AutoCompleteCtrl', AutoCompleteCtrl);

  function AutoCompleteCtrl ($timeout, $q, searchBoxModel) {
    //-- private variables
    var vm = this;

    //-- public variables
    vm.readonly = false;
    vm.selectedItem = null;
    vm.searchState = searchBoxModel.searchState;
    // vm.numberChips = [];
    // vm.numberBuffer = '';
    // vm.searchText = vm.searchState.searchText;

    //-- public methods
    vm.querySearch = querySearch;
    vm.autocompleteRequireMatch = true;
    vm.transformChip = transformChip;

    //-- initialization
    vm.dataElements = loadData();


    //////////////////////////////////////


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
      var results = query ? vm.dataElements.filter(createFilterFor(query)) : [];
      return results;
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
          'name': '2016',
          'type': 'year'
        },
        {
          'name': '2012',
          'type': 'year'
        },
        {
          'name': '2011',
          'type': 'year'
        },
        {
          'name': '2014',
          'type': 'year'
        },
        {
          'name': '2013',
          'type': 'year'
        },
        {
          'name': '2010',
          'type': 'year'
        },
        {
          'name': '2009',
          'type': 'year'
        },
        {
          'name': '2008',
          'type': 'year'
        },
        {
          'name': '2007',
          'type': 'year'
        },
        {
          'name': '2006',
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
