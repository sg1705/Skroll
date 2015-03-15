'use strict';

/**
 * @ngdoc directive
 * @name SkrollApp.directive:fileUpload
 * @description
 * # fileUpload
 */
angular.module('SkrollApp')
  .directive('scrollToParagraph', ['documentModel', function(documentModel) {
    return {
      restricted: 'A',
      link: function(scope, element, attrs) {
        var paragraphId = attrs.scrollToParagraph;
        var para =
        $(element).click(function() {
            var para = $("#"+paragraphId);
            if (para != null) {
                var contentDiv = $("#skrollport");
                console.log(contentDiv.scrollTop());
                $("#skrollport").animate({scrollTop: ($("#skrollport").scrollTop() - 200 + $(para).offset().top)}, "slow");
                $(para).css("background-color","yellow");
                scope.toggleSidenav('left');
            }
          });
        }

    }
  }]);