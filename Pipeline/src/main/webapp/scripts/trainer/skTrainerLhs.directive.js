(function() {
  'use strict';

  /**
   * @ngdoc directive
   * @name myappApp.directive:skTrainerLhs
   * @description
   * # skTrainerLhs
   */
  angular
    .module('app.trainer')
    .directive('skTrainerLhs', SkTrainerLhs);


  function SkTrainerLhs(featureFlags) {

    var directive = {
      templateUrl: 'scripts/trainer/sktrainerlhs.tmpl.html',
      restrict: 'E',
      controller: 'TocCtrl',
      controllerAs: 'ctrl'
    };

    return directive;

  }

})();
