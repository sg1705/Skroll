(function(){

	'use strict';

	/**
	 * @ngdoc directive
	 * @name myappApp.directive:skToolbar
	 * @description
	 * # skTrainerToolbar
	 */
	angular
		.module('app.core')
	  .directive('skToolbar', function () {

	    return {
	      templateUrl: 'scripts/core/toolbar.tmpl.html',
	      restrict: 'E',
	      controller: 'ToolbarCtrl',
	      controllerAs: 'toolbarCtrl'
	    };

	  });

})();
