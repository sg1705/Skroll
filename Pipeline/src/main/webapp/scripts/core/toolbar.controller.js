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
  function ToolbarCtrl($mdSidenav, documentModel, $location) {

    //-- private variables
    var vm = this;

    //-- public methods
    vm.toggleSidenav = toggleSidenav;
    vm.navigateToLanding = navigateToLanding;

    /////////////

    function toggleSidenav(menuId) {
      $mdSidenav(menuId).toggle();
    };

    function navigateToLanding() {
      $location.path('/');
    }


  }

})();