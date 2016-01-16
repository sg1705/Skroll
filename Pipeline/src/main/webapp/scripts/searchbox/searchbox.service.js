(function() {
  'use strict';

  angular
    .module('app.searchbox')
    .service('searchBoxService', SearchBoxService);

  /* @ngInject */
  function SearchBoxService($http, $q, $log) {

    //-- private variables
    //context root of API
    var solrServiceBase = 'http://localhost:8983/solr/autocomplete/select?wt=json&indent=true&group=true&group.field=type&group.limit=5&omitHeader=true&q=name_autocomplete%3A';


    //-- service definition
    var service = {
      getSuggestions: getSuggestions
    };

    return service;

    //////////////

    /**
     * Returns a promise which will fetch the
     * terms for a given document
     **/
    function getSuggestions(query) {
      var deferred = $q.defer();
      /** make a get request */
      $http.get(solrServiceBase + "%22" + query +"%22")
        .success(function(data, status) {
          deferred.resolve(data);
        })
        .error(function(msg, code) {
          deferred.reject(msg);
          $log.error(msg, code);
        });;
      // done with get request
      return deferred.promise;
    }
  };
})();