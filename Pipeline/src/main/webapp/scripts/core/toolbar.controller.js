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
  function ToolbarCtrl($mdSidenav, documentModel, $location, linkService, $mdDialog) {

    //-- private variables
    var vm = this;

    //-- public methods
    vm.toggleSidenav = toggleSidenav;
    vm.navigateToLanding = navigateToLanding;
    vm.openEmailDialog = openEmailDialog;
    vm.showShareDialog = showShareDialog;

    /////////////

    function toggleSidenav(menuId) {
      $mdSidenav(menuId).toggle();
    };

    function navigateToLanding() {
      $location.path('/');
    }

    function openEmailDialog() {
      var activeLink = linkService.getActiveLink(documentModel.documentId);
      var mailBody = 'Here is your link:\n' + activeLink;
      window.open('mailto:?body=' + mailBody, 'MsgWindow',
            'toolbar=no,location=no, status=no,menubar=no,scrollbars=yes,resizable=yes,top=300, left=300,width=550,height=420');
    }

    /**
     * Show dialog
     **/
    function showShareDialog() {
      var activeLink = linkService.getActiveLink(documentModel.documentId);
      var alert = $mdDialog.alert({
        title: 'Copy URL for this document',
        content: activeLink,
        ok: 'Close'
      });
      $mdDialog
        .show(alert)
        .finally(function() {
          alert = undefined;
        });
    }



  }

})();