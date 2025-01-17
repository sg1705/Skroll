(function() {

  'use strict';

  /**
   * @ngdoc service
   * @name SkrollApp.selectionService
   * @description
   * # selectionService
   * Factory that represents selectionService in SkrollApp.
   */

  angular
    .module('app.upload')
    .factory('importService', ImportService);


  /** @ngInject **/
  function ImportService($q, documentService, searchFactory) {

    //-- private variables

    //-- service definition
    var service = {

      //-- service functions
      importDocFromUrl: importDocFromUrl

    }

    return service;

    function importDocFromUrl(url, docType) {
      console.log(url);
      documentModel.reset();
      searchFactory.clear();
      documentModel.viewState.isProcessing = true;
      var deferred = $q.defer();
      documentService.importDoc(url, true, docType)
        .then(function(response) {
          // documentModel.url = url;
          // documentModel.documentId = response.documentId
          // documentModel.p.content = response.html;
          // documentModel.format = response.format;
          // if ((response.inCache == null) || (response.inCache == 'false')) {
          //   documentModel.isPartiallyParsed = true;
          // } else {
          //   documentModel.isPartiallyParsed = false;
          // }
          deferred.resolve(documentModel.isPartiallyParsed);
        });
      return deferred.promise;
    }
  };

})();