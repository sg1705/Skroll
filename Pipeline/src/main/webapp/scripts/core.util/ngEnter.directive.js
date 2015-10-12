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
    .directive('ngEnter', NgEnter);

  /* @ngInject */
  function NgEnter() {

    var directive = {
      restricted: 'A',
      link: link
    }

    return directive;

    //////////

    function link(scope, element, attrs) {
      element.bind("keydown keypress", function(event) {
        if (event.which === 13) {
          scope.$apply(function() {
            scope.$eval(attrs.ngEnter);
          });

          event.preventDefault();
        }
      });
    }
  }

})();