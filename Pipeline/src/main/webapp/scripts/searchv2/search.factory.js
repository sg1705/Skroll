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
    .module('app.search')
    .factory('searchFactory', SearchFactory);


  /* @ngInject */
  function SearchFactory() {

    //-- private variables
    var searchText = '',
      searchActive = false,
      searchResults = [],
      currSelectedIndex = -1;

    //-- service definition
    var service = {
      //-- service variables
      searchState: {
        searchText: searchText,
        searchActive: searchActive,
        currSelectedIndex: currSelectedIndex,
        clear: clear
      },
      searchResults: searchResults
    }

    return service;


    //////////////

    /**
     * Clear search
     **/
    function clear() {
      service.searchState.searchActive = false;
      service.searchState.searchText = '';
      service.searchState.searchResults = [];
    };

  };

})();