(function() {
  'use strict';

  /**
   * @ngdoc function
   * @name skrollApp.controller:LandingCtrl
   * @description
   * # LandingCtrl
   * Controller of the LandingCtrl
   */

  angular
    .module('app.landing')
    .controller('DocListCtrl', DocListCtrl);

  /* @ngInject */
  function DocListCtrl(documentService, $location) {

    //-- private variables
    var vm = this;

    //-- public variables
    vm.userDocumentIds = [];

    //-- public methods
    vm.getDocumentIds = getDocumentIds;
    vm.loadDocument = loadDocument;


    function getDocumentIds() {
      documentService.getDocumentIds().then(function(documentIds) {
        vm.userDocumentIds = documentIds;
      });
    }

    function loadDocument(documentId) {
      $location.path('/view/docId/' + documentId);
    }

    getDocumentIds();
  }

})();