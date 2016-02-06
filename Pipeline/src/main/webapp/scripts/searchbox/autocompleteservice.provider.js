(function() {
  'use strict';

  angular
    .module('app.searchbox')
    .provider('autocompleteService', AutocompleteServiceProvider);

  /* @ngInject */
  function AutocompleteServiceProvider() {

    //-- private variables
    var vm = this;
    var hostname = 'http://localhost:8983';
    vm.contextPath = '';


    //-- public
    this.setHostName = function(hostName) {
      vm.hostName = hostName;
    }

    this.setContextPath = function(contextPath) {
      vm.contextPath = contextPath;
    }


    this.$get = function($http, $q, $log) {
      return {
        'getSuggestions' : getSuggestions
      }

      /**
       * Returns a promise which will fetch the
       * terms for a given document
       **/
      function getSuggestions(query) {
        var deferred = $q.defer();
        /** make a get request */
        $http.get(vm.hostName + vm.contextPath + "%22" + query +"%22")
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

    }

  };
})();