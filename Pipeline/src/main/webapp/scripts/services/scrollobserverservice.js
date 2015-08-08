(function() {
	'use strict';

	/**
	 * @ngdoc service
	 * @name SkrollApp.ScrollObserverService
	 * @description
	 * # ScrollObserverService
	 * Manages all scroll, click, touch events on a paragraph
	 */

	angular
		.module('SkrollApp')
		.factory('ScrollObserverService', ScrollObserverService);

	function ScrollObserverService() {

		//-- private variables
		var listeners = [];

		var service = {
			register: register,
			notify  : notify			
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

})()


