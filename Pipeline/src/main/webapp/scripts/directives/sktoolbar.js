'use strict';

/**
 * @ngdoc directive
 * @name myappApp.directive:skToolbar
 * @description
 * # skTrainerToolbar
 */
angular.module('SkrollApp')
  .directive('skToolbar', function () {
    return {
      templateUrl: 'partials/toolbar.tmpl.html',
      restrict: 'E',
      controller: ToolbarCtrl,
      controllerAs: 'ctrl'
    };
  });