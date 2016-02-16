(function(){

  'use strict';

  /**
   * @ngdoc directive
   * @name app.directive:skFabMenu
   * @description
   * # skFabMenu
   */
  angular
    .module('app.link')
    .directive('skFabMenu', SkFabMenu);

    function SkFabMenu() {

      return {
        template: '<md-button md-theme="greyTheme"\
                        class="md-fab md-mini md-primary" \
                        aria-label="Comment" \
                        ng-click="fabMenuCtrl.clickLink()"> \
                        <md-icon md-svg-src="images/icons/ic_link_24px.svg"></md-icon> \
                      </md-button>',
        restrict: 'E',
        controller: 'FabMenuCtrl',
        controllerAs: 'fabMenuCtrl',
        scope: {},
        bindToController: {
          fabmenu: '='
        }
      };
    };

  angular
    .module('app.link')
    .controller('FabMenuCtrl', FabMenuCtrl);

    /* @ngInject */
    function FabMenuCtrl(selectionService, linkService) {
    //-- private variables
    var vm = this;

    //-- public variables
    vm.clickLink = clickLink;


    //-- methods

    function clickLink() {
      console.log('clicked paraId='+ this.fabmenu.currentParaId);
      selectionService.selectParagraph(this.fabmenu.currentParaId);
      linkService.copyLink();
    }
  }


})();
