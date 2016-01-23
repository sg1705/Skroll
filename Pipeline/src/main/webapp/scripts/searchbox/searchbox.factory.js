(function() {

  'use strict';

  /**
   * @ngdoc service
   * @name SkrollApp.searchFactory
   * @description
   * # searchFactory
   * Factory that represents searchFactory in SkrollApp.
   */

  angular
    .module('app.searchbox')
    .factory('searchBoxModel', SearchBoxModel);


  /* @ngInject */
  function SearchBoxModel() {

    //-- private variables
    var selectedChips = [];
    var searchText = '';

    //-- service definition
    var service = {
      //-- service variables
      searchState: {
        selectedChips: selectedChips,
        searchText : searchText
      },

      //-- service methods
      clear   : clear,
      getText : getText,
      isEmpty : isEmpty,
      updateChip : updateChip

    }

    return service;


    //////////////

    /**
     * Return query string text
     **/
    function getText() {
      var wholeSearchText = JSON.stringify(service.searchState); //.map);(function(elem) { return elem.field1 + "(" + elem.type + ")" }).join(":") + ":" + service.searchState.searchText;
      console.log( "wholeSearchText:" + wholeSearchText);
      return wholeSearchText;
    };

    /**
     * Set query string text
     **/
    function setText(text) {
      this.searchText = text;
    };


    /**
     * Clear search
     **/
    function clear() {
      service.selectedChips = [];
      service.searchState.searchText = '';
    };

    function isEmpty() {
      if ((this.searchState.selectedChips.length == 0) && (this.searchState.searchText == '')) {
        return true;
      }
      return false;
    }

    /**
    * Takes a chip with four variables and replaces the existing chip in the category
    * with the new one.
    *
    * Chip fields: field1, type, field2, id
    */
    function updateChip(chip) {
      //filter chip by types
      var filteredChips = _.filter(vm.searchState.selectedChips, function(c) {
        if (c.type != chip.type)
          return c;
      })
    }
  };

})();