(function() {

  'use strict';

  /**
   * @ngdoc directive
   * @name SkrollApp.directive:skPdfContent
   * @description
   * # fileUpload
   */

  angular
    .module('app.core.pdf')
    .directive('skPdfContent', skPdfContent);

  /* @ngInject */
  function skPdfContent(documentModel, documentService, LHSModel, selectionService, $timeout, $http, $analytics, $window, $q, viewportService) {

    var directive = {
      restricted: 'A',
      controller: 'PdfViewPortCtrl',
      scope: false,
      link: link
    }

    return directive;

    //////

    /**
     * Two paths when a document is loaded.
     * Complete the partially loaded doc
     * Or load the entire document
     **/
    function link(scope, element, attrs, viewCtrl) {

    }

  }

})();