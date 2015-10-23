(function() {
  'use strict';

  /**
   * @ngdoc service
   * @name SkrollApp.clickObserverService
   * @description
   * # clickObserverService
   * Manages all click, touch events on a paragraph
   */

  angular
    .module('app.core.services')
    .factory('httpRequestErrorInterceptor', HttpReqeustErrorInterceptor);

  /* @ngInject */
  function HttpReqeustErrorInterceptor($q, $location, documentModel) {

    var service = {
      responseError : responseError,
      request : request
    };

    return service;


    //-- private methods

    /**
     * Method to handle error
     **/
    function responseError(rejection) {
      // do something on error
      // if ((rejection.status === 500) || (rejection === 404) || (rejection === 0)){
          documentModel.isProcessing = false;
          $location.path('/error/');
      // }
      return $q.reject(rejection);
    };


    function request(config) {
      // config.timeout = 100000;
      return config;
    }

  }

})();