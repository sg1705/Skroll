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
  function ImportCtrl($location, $routeParams, $http) {

    //-- private variables
    var url = $routeParams.q;
    var urlf = url.split('/');
    var fileName = urlf[urlf.length - 1];
    if (url == null) {
      return;
    }
    documentModel.isProcessing = true;

    //////////////

    //-- execute

    console.log(url);
    $http.get('restServices/doc/importDoc?documentId=' + $routeParams.q)
      .success(function(data) {
        console.log(data);
        var docId = data.documentId;
        $location.search({});
        $location.path('/view/docId/' + fileName);
      });
  }

  // var BackdropCtrl = function(documentModel) {
  //   this.isProcessing = documentModel.isProcessing;
  // }

  // angular.module('SkrollApp')
  //   .controller('BackdropCtrl', ['documentModel', BackdropCtrl]);

})();
