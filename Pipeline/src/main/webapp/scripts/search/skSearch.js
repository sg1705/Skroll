(function() {

	'use strict';

	/**
	 * @ngdoc directive
	 * @name myappApp.directive:skSearch
	 * @description
	 * # Directive called sk-search for the searchBox
	 */
	angular
		.module('app.search')
	  .directive('skSearch', SkSearch);

  /** ngInject **/
  function SkSearch() {

	  return {
	    templateUrl: 'scripts/search/search.tmpl.html',
	    restrict: 'E',
	    controller: 'SearchCtrl',
	    controllerAs: 'ctrl'
	  };  	

  }

})();

