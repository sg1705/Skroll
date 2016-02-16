(function() {

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
  function scrollToParagraph(selectionService, scrollObserverService, $analytics, $mdSidenav) {

    var directive = {
      restricted: 'A',
      link: link
    }

    return directive;

    //////////

    function link(scope, element, attrs) {
      var searchText = attrs.searchText;
      var para = $(element).click(function() {
        var paragraphId = attrs.scrollToParagraph;
        $analytics.eventTrack(documentModel.documentId, {
          category: 'toc.navClick',
          label: paragraphId
        });
        scrollObserverService.notify(paragraphId);
        selectionService.scrollToParagraph(paragraphId, searchText);
        if (!$mdSidenav('left').isLockedOpen()) {
          $mdSidenav('left').close();
        }

      });
    }
  }

})();