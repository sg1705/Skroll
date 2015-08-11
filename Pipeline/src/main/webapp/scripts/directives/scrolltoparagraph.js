'use strict';

/**
 * @ngdoc directive
 * @name SkrollApp.directive:fileUpload
 * @description
 * # fileUpload
 */
angular.module('SkrollApp')
  .directive('scrollToParagraph', [ 'documentModel', '$mdSidenav', 
                                    'selectionService', 'ToolbarModel', 'scrollObserverService',
    function(documentModel, $mdSidenav, selectionService, ToolbarModel, scrollObserverService) {
      return {
        restricted: 'A',
        link: function(scope, element, attrs) {
          var paragraphId = attrs.scrollToParagraph;
          var para =
            $(element).click(function() {
              scrollObserverService.notify(paragraphId);
              selectionService.scrollToParagraph(paragraphId);
              scope.$apply();

            });
        }
      }
    }
  ]);