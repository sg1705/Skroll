(function() {

	'use strict';

	/**
	 * @ngdoc directive
	 * @name myappApp.directive:skTrainerToolbar
	 * @description
	 * # Directive called sk-search for the trainer toolbar
	 */
	angular
		.module('app.trainer')
	  .directive('skTrainerToolbar', SkTrainerToolbar);

  /** ngInject **/
  function SkTrainerToolbar() {

	  return {
	    templateUrl: 'scripts/trainer/trainertoolbar.tmpl.html',
	    restrict: 'E',
	    controller: 'TrainerToolbarCtrl',
	    controllerAs: 'ctrl'
	  };  	

  }

})();
