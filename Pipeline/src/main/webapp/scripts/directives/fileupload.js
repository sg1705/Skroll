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
    function(documentModel, ToolbarModel, documentService, LHSModel) {
      return {
          restricted: 'A',
          link: function(scope, element, attrs) {
           $(element).fileupload({
                  dataType: 'text',
                  add: function (e, data) {
                      scope.$apply(function() {
                          scope.isProcessing = true;
                          documentModel.isProcessing = true;
                          documentModel.fileName = data.files[0].name;
                          scope.fileName = data.files[0].name;
                      })
                      ToolbarModel.toolbarInfo.title = data.files[0].name;
                      data.submit();
                  },
                  done: function (e, data) {
                      console.log("Done setting")
                      $("#content").html(data.result);
                      //TODO delete this once we have the end point
                      //populate definitions
                      documentService.getDefinition().then(function(definitions){
                        //create new LHS model items
                        var items = [];
                        for(var ii = 0; ii < definitions.length; ii++) {
                          var item = {
                            itemId: definitions[ii].paragraphId,
                            text: definitions[ii].definition
                          }
                          items.push(item);
                        }
                        LHSModel.sections[0].items = items;
                        // TODO - end delete

                        console.log('items:' + items.length);
                      }, function(msg) {
                        console.log(msg);
                      });

                      //use get terms
                      documentService.getDefinition().then(function(terms){
                        //create new LHS model items
                        LHSModel.model = terms;
                        console.log(terms);
                      }, function(msg) {
                        console.log(msg);
                      });


                      scope.$apply(function() {
                          scope.targetHtml = data.result;
                          scope.isDocAvailable = true;
                          scope.isProcessing = false;
                          documentModel.isDocAvailable = true;
                          documentModel.targetHtml = data.result;
                          documentModel.isProcessing = false;
                          console.log("Done assigning");

                      });
                  },
                  fail: function (e, data) {
                      console.log("failed");
                      console.log(e);
                  }
              });
          }
      }

  }]);

