(function() {

  'use strict';

  /**
   * @ngdoc directive
   * @name myappApp.directive:skRelatedPara
   * @description
   * # Directive called sk-related-para for the relatedPara
   */
  angular
    .module('app.related')
    .directive('skRelatedPara', SkRelatedPara);

  /* ngInject */
  function SkRelatedPara() {

    return {
      templateUrl: 'scripts/related/relatedpara.tmpl.html',
      restrict: 'E',
      controller: 'RelatedParaCtrl',
      controllerAs: 'relatedParaCtrl'
    };

  }

})();
