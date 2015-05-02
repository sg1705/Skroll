'use strict';

/**
 * @ngdoc directive
 * @name SkrollApp.directive:fileUpload
 * @description
 * # fileUpload
 */
angular.module('SkrollApp')
  .directive('fileUpload', 
    ['documentModel', 
    'ToolbarModel', 
    'documentService',
    'LHSModel',
    '$location',
    function(documentModel, ToolbarModel, documentService, LHSModel, $location) {
      return {
          restricted: 'A',
          link: function(scope, element, attrs) {
           $(element).fileupload({
                  dataType: 'text',
                  add: function (e, data) {
                      scope.$apply(function() {
                          documentModel.isProcessing = true;
                          documentModel.fileName = data.files[0].name;
                          scope.fileName = data.files[0].name;
                      })
                      ToolbarModel.toolbarInfo.title = data.files[0].name;
                      data.submit();
                  },
                  done: function (e, data) {
                      var terms;
                      $("#content").html(data.result);
                      //use get terms
                      documentService.getTerms().then(function(terms){
                        //create new LHS model items
                        LHSModel.smodel.terms = terms;
                      }, function(msg) {
                        console.log(msg);
                      });
                      scope.$apply(function() {
                          scope.targetHtml = data.result;
                          scope.isDocAvailable = true;
                          scope.isProcessing = false;
                          LHSModel.model = terms;
                          documentModel.isDocAvailable = true;
                          documentModel.targetHtml = data.result;
                          documentModel.isProcessing = false;
                      });
                      documentModel.documentId = documentModel.fileName;
                      $location.path('/view/docId/' + documentModel.documentId);
                  },
                  fail: function (e, data) {
                      console.log("failed");
                      console.log(e);
                  }
              });
          }
      }

  }]);

