(function() {

	'use strict';

	/**
	 * @ngdoc function
	 * @name skrollApp.controller:ToolbarCtrl
	 * @description
	 * # ToolbarCtrl
	 * Controller of the ToolbarCtrl
	 */

	angular
		.module('app.core')
		.controller('ToolbarCtrl', ToolbarCtrl);

	/* @ngInject */
	function ToolbarCtrl($mdSidenav, ToolbarModel) {

		//-- private variables
		var vm = this;

		//-- public variables
		vm.toolbarInfo = ToolbarModel.toolbarInfo;
		
		//-- public methods
		vm.toggleSidenav = toggleSidenav;


		/////////////

    function toggleSidenav(menuId) {
      $mdSidenav(menuId).toggle();
    };

  }

})();
