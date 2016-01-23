(function(){

  'use strict';

  /**
   * @ngdoc directive
   * @name myappApp.directive:skSearchBox
   * @description
   * # skSearchBox
   */
  angular
    .module('app.searchbox')
      .directive('skSearchBox', SkSearchBox);

    function SkSearchBox() {

      return {
        templateUrl: 'scripts/searchbox/searchbox.tmpl.html',
        restrict: 'E',
        controller: 'AutoCompleteCtrl',
        controllerAs: 'autoCompleteCtrl',
        scope: {
            boxAlign: '@boxAlign'
        }

      };


    };
})();
