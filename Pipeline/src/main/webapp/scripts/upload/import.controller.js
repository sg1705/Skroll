(function() {
  'use strict';

  /**
   * @ngdoc function
   * @name skrollApp.controller:ImportCtrl
   * @description
   * # ImportCtrl
   * Controller of the ImportCtrl
   */

  angular
    .module('app.upload')
    .controller('ImportCtrl', ImportCtrl);

  /* @ngInject */
  function ImportCtrl($location, $routeParams, $http, documentService) {

    //-- private variables
    var url = $routeParams.q;
    if (url == null) {
      return;
    }
    documentModel.isProcessing = true;
    documentModel.url = url;

    //////////////

    //-- execute

    console.log(url);
    documentService.importDoc(url, true)
      .then(function(response) {
        documentModel.documentId = response.documentId
        documentModel.targetHtml = response.html;
        if ((response.inCache == null) || (response.inCache == 'false')) {
          response.inCache = false;
        } else {
          response.inCache = true;
        }
        if (response.inCache) {
          documentModel.isPartiallyParsed = false;
        } else {
          documentModel.isPartiallyParsed = true;
        }
        $location.search({});
        $location.path('/view/docId/' + documentModel.documentId);
      });
  }

})();