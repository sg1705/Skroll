(function() {
  'use strict';

  /**
   * @ngdoc service
   * @name SkrollApp.textSelectionObserverService
   * @description
   * # textSelectionObserverService
   * Manages all click, touch events on a paragraph
   */

  angular
    .module('app.core.services')
    .factory('textSelectionObserverService', TextSelectionObserverService);

  /* @ngInject */
  function TextSelectionObserverService() {

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