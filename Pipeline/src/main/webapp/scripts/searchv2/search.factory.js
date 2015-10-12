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


  /** @ngInject **/
  function SearchFactory() {

    //-- private variables
    var searchText = '',
      searchActive = false,
      searchResults = [];

    //-- service definition
    var service = {
      //-- service variables
      searchState: {
        searchText: searchText,
        searchActive: searchActive
      },
      searchResults: searchResults,
    }

    return service;

  };

})();