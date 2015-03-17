'use strict';

/**
 * @ngdoc directive
 * @name myappApp.directive:skLhs
 * @description
 * # skLhs
 */
angular.module('SkrollApp')
  .directive('skLhs', function () {
    return {
      templateUrl: 'partials/sklhs.tmpl.html',
      restrict: 'E',
      controller: LHSCtrl,
      controllerAs: 'ctrl'
    };
  });

var LHSCtrl = function (LHSModel) {
	this.sections = LHSModel.sections;
}

LHSCtrl.prototype.toggleSection = function(index) {
	this.sections[index].isSelected = !this.sections[index].isSelected;
}

angular.module('SkrollApp')
	.controller('LHSCtrl', LHSCtrl);