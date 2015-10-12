(function() {

  'use strict';

  /**
   * @ngdoc directive
   * @name SkrollApp.directive:fileUpload
   * @description
   * # fileUpload
   */
  angular
    .module('app.upload')
    .directive('skFileUpload', skFileUpload);

  /* @ngInject */
  function skFileUpload(documentModel, documentService, LHSModel, $location) {

    var directive = {
      restricted: 'A',
      link: link
    };

    return directive;

    //////////

    function link(scope, element, attrs) {

      $(element).fileupload({
        dataType: 'text',
        add: add, //see function add()
        done: done, //see function done()
        fail: fail //see function fail()
      });

      function add(e, data) {
        scope.$apply(function() {
          documentModel.isProcessing = true;
        })
        data.submit();
      }

      function done(e, data) {
        var terms;
        $("#content").html(data.result);
        documentModel.documentId = data.jqXHR.getResponseHeader('documentId');
        //use get terms
        documentService.getTerms(documentModel.documentId).then(function(terms) {
          //create new LHS model items
          LHSModel.setTerms(terms);
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
        $location.path('/view/docId/' + documentModel.documentId);
      }

      function fail(e, data) {
        console.log("failed");
        console.log(e);
      }

    }
  }

})();