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
	function ToolbarCtrl($mdSidenav, documentModel, featureFlags, $location) {

		//-- private variables
		var vm = this;

		//-- public variables
		vm.documentId = documentModel.documentId;
		
		//-- public methods
		vm.toggleSidenav = toggleSidenav;

		//-- startup action
		enableTrainerMode($location.search());

		/////////////

    function toggleSidenav(menuId) {
      $mdSidenav(menuId).toggle();
    };

		function enableTrainerMode(queryParams) {
      var flagName = 'trainer';
      var flags = featureFlags.get();
      var flag = _.find(flags, function(item){ 
                                  if (item.key == flagName) {return true;}
                                });
      if (flag != null) {
      		if (queryParams.trainer) {
      			featureFlags.enable(flag);	
      		} else {
      			featureFlags.disable(flag);	
      		}

      }
		}



  }

})();
