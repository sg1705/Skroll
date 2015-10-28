(function() {
  'use strict';

  /**
   * @ngdoc directive
   * @name myappApp.directive:skLhs
   * @description
   * # skLhs
   */
  angular
    .module('app.toc')
    .directive('skLhs', SkLhs);


  function SkLhs(featureFlags) {

    var directive = {
      templateUrl: 'scripts/toc/toc.tmpl.html',
      restrict: 'E',
      controller: 'TocCtrl',
      controllerAs: 'ctrl'
    };

    return directive;

  }

  // angular.module('SkrollApp')
  //   .directive('skLhsLevels', function () {
  //     return {
  //       templateUrl: 'partials/sklhslevels.tmpl.html',
  //       restrict: 'E',
  //       controller: LHSLevelsCtrl,
  //       controllerAs: 'ctrl'
  //     };
  //   });

  // var LHSLevelsCtrl = function (LHSModel) {
  //   this.sections = LHSModel.sections;
  //   this.smodel = LHSModel.smodel;
  //   this.classes = LHSModel.classes;
  // }

  // LHSLevelsCtrl.prototype.toggleSection = function(index) {
  //   this.classes[index].isSelected = !this.classes[index].isSelected;
  // }

  // angular.module('SkrollApp')
  //   .controller('LHSLevelsCtrl', LHSLevelsCtrl);

})();


// 'use strict';

// /**
//  * @ngdoc directive
//  * @name myappApp.directive:skLhs
//  * @description
//  * # skLhs
//  */
// angular
//   .module('SkrollApp')
//   .directive('skLhs', SkLhs);


// function SkLhs() {

//   return {
//     templateUrl: 'partials/sklhs.tmpl.html',
//     restrict: 'E',
//     controller: 'TocCtrl',
//     controllerAs: 'ctrl'
//   };


// };

// angular.module('SkrollApp')
//   .directive('skLhsLevels', function () {
//     return {
//       templateUrl: 'partials/sklhslevels.tmpl.html',
//       restrict: 'E',
//       controller: LHSLevelsCtrl,
//       controllerAs: 'ctrl'
//     };
//   });

// var LHSLevelsCtrl = function (LHSModel) {
//   this.sections = LHSModel.sections;
//   this.smodel = LHSModel.smodel;
//   this.classes = LHSModel.classes;
// }

// LHSLevelsCtrl.prototype.toggleSection = function(index) {
//   this.classes[index].isSelected = !this.classes[index].isSelected;
// }

// angular.module('SkrollApp')
//   .controller('LHSLevelsCtrl', LHSLevelsCtrl);