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
  function ImportCtrl($location, $routeParams, $http, importService) {

    //-- private variables
    var url = $routeParams.q;
    if (url == null) {
      return;
    }
    documentModel.viewState.isProcessing = true;

    //////////////

    //-- execute

    importService
      .importDocFromUrl(url)
      .then(function() {
        $location.search({});
        $location.path('/view/docId/' + documentModel.documentId);
      });
  }

})();