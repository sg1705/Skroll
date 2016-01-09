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
      selectedChips: selectedChips,
      //-- service variables
      searchState: {
        searchText : searchText
      },
      clear: clear,
      getText : getText
    }

    return service;


    //////////////

    /**
     * Clear search
     **/
    function getText() {
      var wholeSearchText = service.selectedChips.map(function(elem) { return elem.name}).join(" ") +service.searchState.searchText;
      console.log( "wholeSearchText:" + wholeSearchText);
      return wholeSearchText;
    };
    /**
     * Clear search
     **/
    function clear() {
      service.selectedChips = [];
      service.searchState.searchText = '';
    };

  };

})();