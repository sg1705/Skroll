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
  function ContextMenuCtrl($mdToast, linkService, selectionService, documentModel) {

  	//-- private variables
  	var vm = this;

  	//-- public methods
  	vm.closeContextMenu = closeContextMenu;
  	vm.copyLink 				= copyLink;

  	////////

  	function copyLink(link) {
      var activeLink = linkService.getActiveLink(documentModel.documentId, selectionService.serializedSelection);
      var shortenedUrl = '';
      linkService.shortenLink(activeLink)
        .then(function(response) {
          console.log(response.result.id);
        }, function(reason) {
          $log.error(reason);
        });
  		$mdToast.hide();
  	}


  	function closeContextMenu() {
  		$mdToast.hide();
  	}
  }

})();