'use strict';

/**
 * @ngdoc directive
 * @name SkrollApp.directive:fileUpload
 * @description
 * # fileUpload
 */
angular.module('SkrollApp')
  .directive('scrollToParagraph', ['documentModel', '$mdSidenav', 'SelectionModel', 'ToolbarModel',
    function(documentModel, $mdSidenav, SelectionModel, ToolbarModel) {
      return {
        restricted: 'A',
        link: function(scope, element, attrs) {
          var paragraphId = attrs.scrollToParagraph;
          var para =
            $(element).click(function() {
              SelectionModel.scrollToParagraph(paragraphId);
            });
        }
      }
    }
  ]);