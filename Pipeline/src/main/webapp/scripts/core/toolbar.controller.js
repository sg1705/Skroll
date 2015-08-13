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


	function ToolbarCtrl($mdSidenav) {

		//-- private variables
		var vm = this;

		//-- public methods
		vm.toggleSidenav = toggleSidenav;


		/////////////

    function toggleSidenav(menuId) {
      $mdSidenav(menuId).toggle();
    };


})();
