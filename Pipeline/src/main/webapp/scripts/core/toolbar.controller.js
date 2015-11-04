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
  function ToolbarCtrl($mdSidenav, documentModel, $location, linkService, $mdDialog, $mdMedia) {

    //-- private variables
    var vm = this;

    //-- public methods
    vm.toggleSidenav = toggleSidenav;
    vm.navigateToLanding = navigateToLanding;
    vm.openEmailDialog = openEmailDialog;
    vm.showShareDialog = showShareDialog;
    vm.showSendFeedbackDialog = showSendFeedbackDialog;

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
      vm.activeLink = linkService.getActiveLink(documentModel.documentId);
      var alert = {
        template: '\
                  <md-dialog-content class="md-dialog-content" role="document" tabindex="-1" id="dialog_1"> \
                    <h2 class="md-title ng-binding">Share via URL</h2> \
                    <div class="md-dialog-content-body" md-template="::dialog.mdContent"> \
                      <p id="sharelink">{{copydialogCtrl.activeLink}}</p> \
                    </div> \
                  </md-dialog-content> \
                  <div class="md-actions"> \
                    <button class="md-primary md-button md-default-theme md-ink-ripple" ng-click="copydialogCtrl.hide()"> \
                      <span class="ng-binding ng-scope">Close</span> \
                    </button> \
                  </div>',
        clickOutsideToClose: true,
        locals: {
          activeLink: vm.activeLink
        },
        /* @ngInject */
        controller: function($mdDialog) {
          this.hide = function() {
            $mdDialog.hide();
          }
        },
        bindToController: true,
        controllerAs: 'copydialogCtrl',
        openFrom: angular.element('#sk-toolbar-share-button'),
        closeTo: angular.element('#sk-toolbar-share-button'),
        onComplete: function() {
          var element = document.getElementById("sharelink");
          var range = rangy.createRange();
          range.selectNode(element)
          rangy.getSelection().setSingleRange(range);
        }

      }

      $mdDialog
        .show(alert)
        .finally(function() {
          console.log('hidden');
          alert = undefined;
        });
    }

    /**
     * Show send feedback dialog
     **/
    function showSendFeedbackDialog() {
      var alert = {
        templateUrl: 'scripts/core/sendfeedback.tmpl.html',
        clickOutsideToClose: true,
        locals: {
          //activeLink: vm.activeLink
        },
        /* @ngInject */
        controller: function($mdDialog) {
          this.hide = function() {
            $mdDialog.hide();
          }
        },
        bindToController: true,
        controllerAs: 'copydialogCtrl',
        openFrom: angular.element('#sk-toolbar-send-feedback-button'),
        closeTo: angular.element('#sk-toolbar-send-feedback-button'),
        onComplete: function() {

        }

      }

      $mdDialog
        .show(alert)
        .finally(function() {
          console.log('hidden');
          alert = undefined;
        });
    }


  }

})();
