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
  function skContent(documentModel, documentService, LHSModel, selectionService, $timeout, $http) {

    var directive = {
      restricted: 'E',
      transclude: true,
      link: link
    }

    return directive;

    //////

    /**
    * Two paths when a document is loaded.
    * Complete the partially loaded doc
    * Or load the entire document
    **/
    function link(scope, element, attrs) {
      if (documentModel.documentId != null) {
        documentModel.isProcessing = true;
        if (documentModel.isPartiallyParsed) {
          console.log('partially parsed');
          element.replaceWith(documentModel.targetHtml);
          documentModel.isProcessing = false;
          showGradually();
          documentService.importDoc(documentModel.url, false)
            .then(function(data) {
              documentModel.isPartiallyParsed = false;
              return documentService.getTerms(documentModel.documentId);              
            })
            .then(function(terms) {
              LHSModel.setTerms(terms);
              console.log(terms);
              LHSModel.setYOffsetForTerms(LHSModel.smodel.terms);
            });
        } else {
          documentService.loadDocument(documentModel.documentId)
          .then(function(contentHtml) {
            documentModel.targetHtml = contentHtml;
            element.replaceWith(documentModel.targetHtml);
            documentModel.isProcessing = false;
            if ((selectionService.serializedSelection === undefined) || (selectionService.serializedSelection == "undefined")) {
              showGradually();  
            } else {
              showEntireDoc();
            }
            return documentService.getTerms(documentModel.documentId);
          })
          .then(function(terms) {
            LHSModel.setTerms(terms);
            console.log(terms);
            $timeout(timeout, 0); //@see function timeout()

            function timeout() {
              console.log(selectionService.serializedSelection);
              if ((selectionService.serializedSelection === undefined) || (selectionService.serializedSelection == "undefined")) {

              } else {
                selectionService.scrollToSelection(selectionService.serializedSelection);
              }
              documentModel.isProcessing = false;
              //calculate offsets for headers
              //iterate over each term to find Y offset
              LHSModel.setYOffsetForTerms(LHSModel.smodel.terms);

              documentModel.isProcessing = false;
            }
          }, function(data, status) {
              console.log(status);
          });
        }
      }
    }

    function showGradually() {
      var elements = $('[id^="p_"]')
      $.each(elements, function(index) {
          if (!($(elements[index]).attr('id').indexOf("p_12") === 0)) {
            $timeout(function(){
              $(elements[index]).show();  
            }, 0) 
          }
      });      
    };

    function showEntireDoc() {
      var elements = $('[id^="p_"]')
      $.each(elements, function(index) {
        $(elements[index]).show();  
      });      
    };



  }

})();