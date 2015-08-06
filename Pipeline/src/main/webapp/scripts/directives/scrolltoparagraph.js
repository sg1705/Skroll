'use strict';

/**
 * @ngdoc directive
 * @name SkrollApp.directive:fileUpload
 * @description
 * # fileUpload
 */
angular.module('SkrollApp')
  .directive('scrollToParagraph', [ 'documentModel', '$mdSidenav', 
                                    'SelectionModel', 'ToolbarModel', 'ScrollObserverService',
    function(documentModel, $mdSidenav, SelectionModel, ToolbarModel, ScrollObserverService) {
      return {
        restricted: 'A',
        link: function(scope, element, attrs) {
          var paragraphId = attrs.scrollToParagraph;
          var para =
            $(element).click(function() {
              ScrollObserverService.notify(paragraphId);
              SelectionModel.scrollToParagraph(paragraphId);
              scope.$apply();

            });
        }
      }
    }
  ]);