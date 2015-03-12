'use strict';

/**
 * @ngdoc directive
 * @name SkrollApp.directive:fileUpload
 * @description
 * # fileUpload
 */
angular.module('SkrollApp')
  .directive('fileUpload', ['documentModel', function(documentModel) {
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
                      data.submit();
                  },
                  done: function (e, data) {
                      console.log("Done setting");
                      $("#content").html(data.result);
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

