'use strict';

/**
 * @ngdoc directive
 * @name myappApp.directive:skTrainerToolbar
 * @description
 * # skTrainerToolbar
 */
angular.module('SkrollApp')
  .directive('skTrainerToolbar', function () {
    return {
      templateUrl: 'partials/trainerToolbar.tmpl.html',
      restrict: 'E',
      controller: TrainerCtrl,
      controllerAs: 'ctrl'
    };
  });