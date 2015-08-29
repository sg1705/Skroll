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
    var urlf = url.split('/');
    var fileName = urlf[urlf.length - 1];
    if (url == null) {
      return;
    }
    documentModel.isProcessing = true;
    documentModel.url = url;

    //////////////

    //-- execute

    console.log(url);
    documentService.importDoc(url, true)
      .then(function(data) {
        documentModel.documentId = fileName;
        documentModel.targetHtml = data;
        documentModel.isPartiallyParsed = true;
        $location.search({});
        $location.path('/view/docId/' + fileName);
      });
  }

})();
