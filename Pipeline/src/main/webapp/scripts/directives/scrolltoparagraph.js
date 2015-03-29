'use strict';

/**
 * @ngdoc directive
 * @name SkrollApp.directive:fileUpload
 * @description
 * # fileUpload
 */
angular.module('SkrollApp')
  .directive('scrollToParagraph', ['documentModel', '$mdSidenav', 'SelectionModel', function(documentModel, $mdSidenav, SelectionModel) {
    return {
      restricted: 'A',
      link: function(scope, element, attrs) {
        var paragraphId = attrs.scrollToParagraph;
        
        var para =
          $(element).click(function() {
            var para = $("#" + paragraphId);
            $("#" + SelectionModel.paragraphId).css("background-color", "");
            if (para != null) {
              var contentDiv = $("#skrollport");
              $("#skrollport").animate({
                scrollTop: ($("#skrollport").scrollTop() - 200 + $(
                  para).offset().top)
              }, "slow");
              $(para).css("background-color", "yellow");
              $mdSidenav('left').toggle();
              SelectionModel.paragraphId = paragraphId;
            }
          });
      }

    }
  }]);