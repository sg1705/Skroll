(function(){
  
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
  function skContent(documentModel, documentService, LHSModel, selectionService, $timeout) {

    var directive = {
      restricted: 'E',
      transclude: true,
      link: link
    }

    return directive;

    //////

    function link(scope, element, attrs) {
      if (documentModel.documentId != null) {
        documentModel.isProcessing = true;
        documentService.loadDocument(documentModel.documentId)
        .then(function(contentHtml) {
          documentModel.targetHtml = contentHtml;
          //$(element).html(documentModel.targetHtml)
          return documentService.getTerms(documentModel.documentId);
        })
        .then(function(terms) {
          LHSModel.setTerms(terms);
          console.log(terms);
          element.replaceWith(documentModel.targetHtml);
          $timeout(timeout, 0); //@see function timeout()
          //documentModel.isProcessing = false;

          function timeout() {
            console.log(selectionService.serializedSelection);
            if ((selectionService.serializedSelection === undefined) || (selectionService.serializedSelection == "undefined")) {

            } else {
              selectionService.scrollToSelection(selectionService.serializedSelection);
            }
            documentModel.isProcessing = false;
            //calculate offsets for headers
            //iterate over each term to find Y offset
            LHSModel.smodel.terms = _.map(LHSModel.smodel.terms, function(term) {
              term.offsetY =  $("#"+term.paragraphId).scrollTop();
              return term;
            });                            
          }
        }, function(data, status) {
            console.log(status);
        });
      }
    }
  }

})();