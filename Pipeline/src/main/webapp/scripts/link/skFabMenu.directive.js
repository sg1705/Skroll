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
        restrict: 'E'
      };
    };

})();
