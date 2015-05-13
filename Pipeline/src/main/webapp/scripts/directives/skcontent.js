'use strict';

/**
 * @ngdoc directive
 * @name SkrollApp.directive:fileUpload
 * @description
 * # fileUpload
 */
angular.module('SkrollApp')
  .directive('skContent', ['documentModel', 'documentService', 'LHSModel',
    function(documentModel, documentService, LHSModel) {
    return {
      restricted: 'E',
      transclude: true,
      link: function(scope, element, attrs) {
          if (documentModel.documentId != null) {
            documentService.loadDocument(documentModel.documentId).then(angular.bind(this, function(contentHtml) {
              documentModel.targetHtml = contentHtml;

              //$(element).html(documentModel.targetHtml)
                documentService.getTerms().then(function(terms) {
                LHSModel.smodel.terms = terms;
                console.log(terms);
                LHSModel.createLevels();
                element.replaceWith(documentModel.targetHtml);
                documentModel.isProcessing = false;
              }, function(data, status) {
                console.log(status);
              });
          }));
        }
      }
    }
  }]);