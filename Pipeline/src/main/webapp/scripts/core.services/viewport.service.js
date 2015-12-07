(function() {

  'use strict';

  /**
   * @ngdoc service
   * @name SkrollApp.viewPortService
   * @description
   * # viewPortService
   * Factory that represents viewPortService in SkrollApp.
   */

  angular
    .module('app.core.services')
    .factory('viewportService', ViewPortService);

  /* @ngInject */
  function ViewPortService() {

    //-- private variables

    //contains - offsetTop and offsetLeft
    var viewportOffset;

    //-- service definition
    var service = {

      //-- service variables
      viewportOffset: viewportOffset,

      //-- service functions
    }

    return service;

  };

})();