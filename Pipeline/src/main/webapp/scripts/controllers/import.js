'use strict';

/**
 * @ngdoc function
 * @name skrollApp.controller:ImportCtrl
 * @description
 * # ImportCtrl
 * Controller of the ImportCtrl
 */

var ImportCtrl = function($location, $routeParams, $http) {
  var url = $routeParams.q;
  var urlf = url.split('/');
  var fileName = urlf[urlf.length - 1];
  if (url == null) {
    return;
  }
  documentModel.isProcessing = true;
  console.log(url);
  $http.get('restServices/jsonAPI/importDoc?documentId=' + $routeParams.q)
    .success(function(data) {
      console.log(data);
      var docId = data.documentId;
      $location.search({});
      $location.path('/view/docId/' + fileName);
    });
}

angular.module('SkrollApp')
  .controller('ImportCtrl', ['$location', '$routeParams', '$http', ImportCtrl]);

var BackdropCtrl = function(documentModel) {
  this.isProcessing = documentModel.isProcessing;
}

angular.module('SkrollApp')
  .controller('BackdropCtrl', ['documentModel', BackdropCtrl]);

