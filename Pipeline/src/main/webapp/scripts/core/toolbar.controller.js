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
	function ToolbarCtrl($mdSidenav, documentModel) {

		//-- private variables
		var vm = this;

		//-- public variables
		vm.documentId = documentModel.documentId;
		
		//-- public methods
		vm.toggleSidenav = toggleSidenav;

		/////////////

    function toggleSidenav(menuId) {
      $mdSidenav(menuId).toggle();
    };




  }

})();
