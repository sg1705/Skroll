(function() {

  'use strict';

  /**
   * @ngdoc directive
   * @name SkrollApp.directive:ngEnter
   * @description
   * # do something when enter is pressed
   */
  angular
    .module('app.core.util')
    .directive('inputClear', inputClear);

  // Borrowed this excellent implementation from http://plnkr.co/edit/Dje0l1?p=preview
  function inputClear() {
    return {
      restrict: 'A',
      compile: function(element, attrs) {
        var color = attrs.inputClear;
        var style = color ? "color:" + color + ";" : "";
        var action = attrs.ngModel + " = ''";
        element.after(
          '<md-button class="animate-show md-icon-button md-accent"' +
          'ng-show="' + attrs.ngModel + '" ng-click="' + action + '"' +
          'style="position: absolute; top: 0px; right: -6px; margin: 0px 0px;">' +
          '<div style="' + style + '">x</div>' +
          '</md-button>');
      }
    };
  }
})();