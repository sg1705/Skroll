(function() {
  'use strict';

  angular
    .module('app.landing')
    .service('secSearchService', SecSearchService);

  /** @ngInject **/

  function SecSearchService($http, $q, $log) {

    //-- private variables
    //context root of API
    var secSearchServiceBase   = 'restServices/search/';
    //var secSearchServiceBase   = 'http://www.sec.gov/cgi-bin/srch-edgar?&output=atom&first=2015&last=2015';
    //http://www.sec.gov/cgi-bin/srch-edgar?text=google&output=atom
    //http://www.sec.gov/cgi-bin/srch-edgar?text=google&start=1&count=80&first=2015&last=2015&output=atom

    //-- service definition
    var service = {

      getSearchResults  : getSearchResults,
      getIndexHtml      : getIndexHtml

    };

    return service;
    
    //////////////


    /**
    * Returns a promise to retrieve search results from SEC.gov
    **/
    function getSearchResults(searchString) {
      $log.debug("Searching SEC.gov for:" + searchString);
      var deferred = $q.defer();
      $http.get(secSearchServiceBase + 'searchSec?text=' + searchString)
        .success(function(data) {
          deferred.resolve(data);
        })
        .error(function(msg, status) {
          deferred.reject(msg);
        })
      return deferred.promise;
    };

    /**
    * Returns a promise to retrieve index html
    **/
    function getIndexHtml(searchString) {
      $log.debug("Fetching Indexhtml for:" + searchString);
      var deferred = $q.defer();
      $http.get(secSearchServiceBase + 'fetchIndex?url=' + searchString)
        .success(function(data) {
          deferred.resolve(data);
        })
        .error(function(msg, status) {
          deferred.reject(msg);
        })
      return deferred.promise;
    };



  };
})();