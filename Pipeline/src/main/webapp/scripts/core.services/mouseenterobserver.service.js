(function() {
  'use strict';

  /**
   * @ngdoc service
   * @name SkrollApp.mouseEnterObserverService
   * @description
   * # mouseEnterObserverService
   * Manages all click, touch events on a paragraph
   */

  angular
    .module('app.core.services')
    .factory('mouseEnterObserverService', MouseEnterObserverService);

  /* @ngInject */
  function MouseEnterObserverService() {

    //-- private variables
    var listeners = [];

    var service = {
      register: register,
      notify: notify
    };

    return service;


    //-- private methods

    /**
     * Method to register listeners
     **/
    function register(callback) {
      listeners.push(callback);
    };

    /**
     * Method to notify all listeners
     **/
    function notify(args) {
      listeners.forEach(function(cb) {
        cb(args);
      });
    };

  }

})();