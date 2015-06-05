'use strict';

/**
 * @ngdoc directive
 * @name SkrollApp.directive:fileUpload
 * @description
 * # fileUpload
 */
angular.module('SkrollApp')
  .directive('skContent', ['documentModel', 'documentService', 'LHSModel', 'SelectionModel', '$timeout',
    function(documentModel, documentService, LHSModel, SelectionModel, $timeout) {
    return {
      restricted: 'E',
      transclude: true,
      // compile: function(element, attributes) {

      //   return {
      //     pre: function(scope, element, attrs) {
      //         if (documentModel.documentId != null) {
      //           documentModel.isProcessing = true;
      //           documentService.loadDocument(documentModel.documentId).then(angular.bind(this, function(contentHtml) {
      //             documentModel.targetHtml = contentHtml;
      //             //$(element).html(documentModel.targetHtml)
      //               documentService.getTerms().then(function(terms) {
      //               LHSModel.smodel.terms = terms;
      //               console.log(terms);
      //               element.replaceWith(documentModel.targetHtml);
      //               ToolbarModel.toolbarInfo.title = documentModel.documentId;
      //               documentModel.isProcessing = false;
      //             }, function(data, status) {
      //               console.log(status);
      //             });
      //         }));
      //       }
      //     },

      //     post: function(scope, element, attrs) {
      //       $timeout(function() {
      //         if (SelectionModel.serializedParagraphId != null) {
      //           console.log(SelectionModel.serializedParagraphId);
      //           SelectionModel.scrollToSelection(SelectionModel.serializedParagraphId);
      //           console.log("From sk-content:" + SelectionModel.serializedParagraphId);
      //         }
      //         documentModel.isProcessing = false;
      //       }, 10000);
      //     }

      //   }
      // }
      link: function(scope, element, attrs) {
          if (documentModel.documentId != null) {
            documentModel.isProcessing = true;
            documentService.loadDocument(documentModel.documentId).then(angular.bind(this, function(contentHtml) {
              documentModel.targetHtml = contentHtml;

              //$(element).html(documentModel.targetHtml)
                documentService.getTerms().then(function(terms) {
                LHSModel.smodel.terms = terms;
                console.log(terms);
                element.replaceWith(documentModel.targetHtml);
                ToolbarModel.toolbarInfo.title = documentModel.documentId;
                $timeout(function() {
                  console.log(SelectionModel.serializedParagraphId);
                  if ((SelectionModel.serializedParagraphId === undefined) || (SelectionModel.serializedParagraphId == "undefined")) {
                    
                  } else {
                    SelectionModel.scrollToSelection(SelectionModel.serializedParagraphId);
                  }
                  documentModel.isProcessing = false;
                }, 0);
                //documentModel.isProcessing = false;
              }, function(data, status) {
                console.log(status);
              });
          }));
        }
      }
    }
  }]);