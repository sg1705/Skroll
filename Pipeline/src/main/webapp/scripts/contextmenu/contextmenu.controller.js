(function() {

	'use strict';

	/**
	 * @ngdoc service
	 * @name myappApp.ContextMenuCtrl
	 * @description
	 * # ContextMenuCtrl
	 * ContextMenuCtrl in the SkrollApp.
	 */



	angular
		.module('app.contextmenu')
		.controller('ContextMenuCtrl' , ContextMenuCtrl);

	/* @ngInject */
  function ContextMenuCtrl($mdToast) {

  	//-- private variables
  	var vm = this;

  	//-- public methods
  	vm.closeContextMenu = closeContextMenu;
  	vm.copyLink 				= copyLink;

  	////////

  	function copyLink(link) {
  		$mdToast.hide();
  	}


  	function closeContextMenu() {
  		$mdToast.hide();
  	}
  }

})();