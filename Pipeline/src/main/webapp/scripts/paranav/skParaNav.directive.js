(function(){

  'use strict';

  /**
   * @ngdoc directive
   * @name app.directive:skParaNav
   * @description
   * # skParaNav
   */
  angular
    .module('app.paranav')
      .directive('skParaNav', SkParaNav);

    function SkParaNav() {

      return {
        templateUrl: 'scripts/searchbox/searchbox.tmpl.html',
        restrict: 'E',
        controller: 'AutoCompleteCtrl',
        controllerAs: 'autoCompleteCtrl',
        scope: {},
      };

      function template(element, attrs) {

      }


    };
})();
