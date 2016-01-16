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
      clear: clear,
      getText : getText
    }

    return service;


    //////////////

    /**
     * Return query string text
     **/
    function getText() {
      var wholeSearchText = service.selectedChips.map(function(elem) { return elem.name}).join(" ") +service.searchState.searchText;
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

  };

})();