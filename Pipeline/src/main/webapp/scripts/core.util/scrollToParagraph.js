(function(){

  'use strict';

  /**
   * @ngdoc directive
   * @name SkrollApp.directive:fileUpload
   * @description
   * # fileUpload
   */
  angular
    .module('app.core.util')
    .directive('scrollToParagraph', scrollToParagraph);

  /* @ngInject */  
  function scrollToParagraph(selectionService, scrollObserverService, $analytics) {

    var directive = {
      restricted: 'A',
      link: link
    }

    return directive;

    //////////
    
    function link(scope, element, attrs) {
      var paragraphId = attrs.scrollToParagraph;
      var para = $(element).click(function() {
        $analytics.eventTrack(documentModel.documentId, { category: 'toc.navClick', label: paragraphId });
        scrollObserverService.notify(paragraphId);
        selectionService.scrollToParagraph(paragraphId);
        scope.$apply();
      });
    }
  }

})();