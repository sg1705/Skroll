(function() {

  'use strict';

  /**
   * @ngdoc directive
   * @name SkrollApp.directive:fileUpload
   * @description
   * # fileUpload
   */

  angular
    .module('app.core')
    .directive('skContent', skContent);

  /* @ngInject */
  function skContent(documentModel, documentService, LHSModel, selectionService, $compile) {

    var directive = {
      restricted: 'E',
      link: link
    }

    return directive;

    function link(scope, element, attrs, contentCtrl) {
      console.log('in skFirstContent with documentId:' + documentModel.documentId);
      if (!documentModel.isPartiallyParsed) {
        documentService
          .loadDocument(documentModel.documentId)
          .then(routeToViewPort);
      } else {
        routeToViewPort();
      }

      function routeToViewPort() {
        if (documentModel.format == 0) {
          var htmlViewPort = '<iframe id="docViewIframe" \
                                scrolling="yes" \
                                frameborder="0" \
                                noborder="noborder" \
                                style="overflow: hidden;-webkit-overflow-scrolling: touch;" \
                                src="about:blank" \
                                sk-html-content> \
                              </iframe> \
                              <sk-fab-menu feature-flag="fab.link" style="position:fixed; opacity: 0" id="skFabMenu"><sk-fab-menu/>';

          var e = $compile(htmlViewPort)(scope);
          element.replaceWith(e);
        } else {
          console.log('processing pdf');
          var pdfjs = '<div ng-controller="PdfViewPortCtrl as pdfviewportCtrl"> \
            <pdfjs-viewer src="/restServices/doc/content?documentId=' + documentModel.documentId + '" \
            download="true" print="false" open="false" cmap-dir="/pdf/cmaps" \
            image-dir="/pdf/images" sk-pdf-content></pdfjs-viewer></div>';
          var e = $compile(pdfjs)(scope);
          element.replaceWith(e);
        }
      }


    }

  }

})();