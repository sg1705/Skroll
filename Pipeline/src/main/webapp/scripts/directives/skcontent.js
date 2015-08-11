'use strict';

/**
 * @ngdoc directive
 * @name SkrollApp.directive:fileUpload
 * @description
 * # fileUpload
 */
angular.module('SkrollApp')
  .directive('skContent', [ 'documentModel', 'documentService', 'LHSModel', 
                            'SelectionModel', '$timeout', 'ToolbarModel',
    function(documentModel, documentService, LHSModel, SelectionModel, $timeout, ToolbarModel) {
      return {
        restricted: 'E',
        transclude: true,
        link: function(scope, element, attrs) {
          if (documentModel.documentId != null) {
            documentModel.isProcessing = true;
            documentService.loadDocument(documentModel.documentId).then(angular.bind(this, function(contentHtml) {
              documentModel.targetHtml = contentHtml;

              //$(element).html(documentModel.targetHtml)
              documentService.getTerms(documentModel.documentId).then(function(terms) {
                LHSModel.setTerms(terms);
                console.log(terms);
                element.replaceWith(documentModel.targetHtml);
                ToolbarModel.toolbarInfo.title = documentModel.documentId;
                $timeout(function() {
                  console.log(SelectionModel.serializedSelection);
                  if ((SelectionModel.serializedSelection === undefined) || (SelectionModel.serializedSelection == "undefined")) {

                  } else {
                    SelectionModel.scrollToSelection(SelectionModel.serializedSelection);
                  }
                  documentModel.isProcessing = false;
                  //calculate offsets for headers
                  //iterate over each term to find Y offset
                  LHSModel.smodel.terms = _.map(LHSModel.smodel.terms, function(term) {
                    term.offsetY =  $("#"+term.paragraphId).scrollTop();
                    return term;
                  });

                  
                }, 0);
                //documentModel.isProcessing = false;
                ToolbarModel.updateBenchmark(documentService);
              }, function(data, status) {
                console.log(status);
              });
            }));
          }
        }
      }
    }
  ]);