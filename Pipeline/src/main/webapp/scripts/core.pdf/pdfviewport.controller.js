(function() {
  /**
   * @ngdoc function
   * @name skrollApp.controller:PdfViewPortCtrl
   * @description
   * # PdfViewPortCtrl
   * Controller of the PdfViewPortCtrl
   */


  angular
    .module('app.core.pdf')
    .controller('PdfViewPortCtrl', PdfViewPortCtrl);

  /* @ngInject */
  function PdfViewPortCtrl($routeParams, selectionService) {

    //-- private variables
    var vm = this;

    //-- public methods

    //-- initialization
    // documentModel.documentId = $routeParams.docId;
    selectionService.serializedSelection = decodeURIComponent(decodeURIComponent($routeParams.linkId));


    /////////////

  }

})();