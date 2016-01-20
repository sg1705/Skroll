(function () {
  'use strict';


  angular
      .module('app.searchbox')
      .controller('AutoCompleteCtrl', AutoCompleteCtrl);

  function AutoCompleteCtrl ($timeout, $q, searchBoxModel, searchBoxService, featureFlags) {

    //-- private variables

    var vm = this;

    //-- public variables
    vm.readonly = false;
    vm.selectedItem = null;
    vm.searchState = searchBoxModel.searchState;

    //-- public methods
    vm.querySearch = querySearch;
    vm.dataElements = loadData();
    vm.selectedChips = searchBoxModel.searchState.selectedChips;
    // vm.numberChips = [];
    // vm.numberChips2 = [];
    // vm.numberBuffer = '';
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
      return { field1: chip, type: 'new' }
    }

    /**
     * Search for sec filing.
     */
    function querySearch (query) {
      if (featureFlags.isOn('solr.autocomplete')) {
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
      } else {
        var results = query ? vm.dataElements.filter(createFilterFor(query)) : [];
        return results;
      }

    }

    /**
     * Create filter function for a query string
     */
    function createFilterFor(query) {
      var lowercaseQuery = angular.lowercase(query);

      return function filterFn(element) {
        return (element._field1.indexOf(lowercaseQuery) === 0) ||
            (element._field2.indexOf(lowercaseQuery) === 0);
      };

    }


    function loadData() {
      var elements = [
        {
          'id'      : '00001288776',
          'field1'  : 'GOOG',
          'field2'  : 'Google, Inc',
          'type'    : 'company'
        },
        {
          'id'      : '0001090872',
          'field1'  : 'AGLN',
          'field2'  : 'Agilent, Inc',
          'type'    : 'company'
        },
        {
          'id'      : '0001166691',
          'field1'  : 'CMST',
          'field2'  : 'Comcast, Inc',
          'type'    : 'company'
        },
        {
          'id'      : 'F-1',
          'field1'  : '10-K',
          'field2'  : '',
          'type'    : 'formtype'
        },
        {
          'id'      : '1',
          'field1'  : 'Proxy',
          'field2'  : '',
          'type'    : 'category'
        },
       {
          'id'      : '2',
          'field1'  : 'Underwriting Agreement',
          'field2'  : '',
          'type'    : 'category'
        },
       {
          'id'      : '3',
          'field1'  : 'Credit Agreement',
          'field2'  : '',
          'type'    : 'category'
        },
        {
          'id'      : '4',
          'field1'  : 'Plans of Reorganization, Merger or Acquisition',
          'field2'  : '',
          'type'    : 'category'
        },
        {
          'id'      : '5',
          'field1'  : 'Articles of Incorporation and bylaw',
          'field2'  : '',
          'type'    : 'category'
        },
        {
          'id'      : '6',
          'field1'  : 'Indenture',
          'field2'  : '',
          'type'    : 'category'
        },
        {
          'id'      : '7',
          'field1'  : 'Legal Opinion',
          'field2'  : '',
          'type'    : 'category'
        },
        {
          'id'      : '8',
          'field1'  : 'Tax Opinion',
          'field2'  : '',
          'type'    : 'category'
        },
,
        {
          'id'      : '9',
          'field1'  : 'Material Contract',
          'field2'  : '',
          'type'    : 'category'
        },
        {
          'id'      : '2',
          'field1'  : 'Proxy',
          'field2'  : '',
          'type'    : 'category'
        },
        {
          'id'      : '3',
          'field1'  : 'Underwriting Agreement',
          'field2'  : '',
          'type'    : 'category'
        }
                        
      ];

      // return elements;

      return elements.map(function (element) {
        element._field1 = element.field1.toLowerCase();
        element._field2 = element.field2.toLowerCase();
        return element;
      });
    }
  }
})();
