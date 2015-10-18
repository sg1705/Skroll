(function() {
  'use strict';

  angular
    .module('app.core.services')
    .service('documentService', DocumentService);

  /* @ngInject */
  function DocumentService($http, $q, $log) {

    //-- private variables
    //context root of API
    var documentServiceBase = 'restServices/doc/';
    var instrumentServiceBase = 'restServices/instrument/';


    //-- service definition
    var service = {

      getDocumentIds: getDocumentIds,
      loadDocument: loadDocument,
      importDoc: importDoc,
      getIndex: getIndex,

      getParagraphJson: getParagraphJson,
      getTerms: getTerms,
      unObserve: unObserve,
      updateTerms: updateTerms,

      getBenchmarkScore: getBenchmarkScore,
      saveAsBenchmark: saveAsBenchmark
    };

    return service;

    //////////////


    /**
     * Returns a promise to retrieves Json for a given paragraph
     **/
    function getParagraphJson(documentId, paragraphId) {
      $log.debug("Fetching json for paragraphId:" + paragraphId);
      var deferred = $q.defer();
      $http.get(instrumentServiceBase + 'getParagraphJson?paragraphId=' +
        paragraphId + '&documentId=' + documentId)
        .success(function(data) {
          deferred.resolve(data);
        })
        .error(function(msg, status) {
          deferred.reject(msg);
        })
      return deferred.promise;
    };



    /**
     * Returns a promise which will fetch the
     * terms for a given document
     **/
    function getTerms(documentId) {
      var deferred = $q.defer();
      /** make a get request */
      $http.get(documentServiceBase + 'getTerms' + '?documentId=' + documentId)
        .success(function(data, status) {
          deferred.resolve(data);
        })
        .error(function(msg, code) {
          deferred.reject(msg);
          $log.error(msg, code);
        });;
      // done with get request
      return deferred.promise;
    }


    /**
     * Returns a promise to update terms for a given document
     **/
    function updateTerms(documentId, terms) {
      var deferred = $q.defer();
      /** make a get request */
      $http.post(documentServiceBase + 'updateTerms' + '?documentId=' + documentId, terms)
        .success(function(data, status) {
          deferred.resolve(data);
        })
        .error(function(msg, code) {
          deferred.reject(msg);
          $log.error(msg, code);
        });;
      /** done with get request */
      return deferred.promise;
    };


    /**
     * Returns a promise to update terms for a given document
     **/
    function importDoc(url, partiallyParse) {
      var deferred = $q.defer();
      /** make a get request */
      $http.get(documentServiceBase + 'importDoc' + '?&partialParse=' + partiallyParse + '&documentId=' + url)
        .success(function(data, status, headers) {
          var resp = {};
          resp.html = data;
          resp.documentId = headers('documentId');
          resp.inCache = headers('inCache');
          deferred.resolve(resp);
        })
        .error(function(msg, code) {
          deferred.reject(msg);
          $log.error(msg, code);
        });;
      /** done with get request */
      return deferred.promise;
    };



    /**
     * Returns a promise to unobserve a paragraph
     **/
    function unObserve(documentId, terms) {
      var deferred = $q.defer();
      /** make a get request */
      $http.post(documentServiceBase + 'unObserve' + '?documentId=' + documentId, terms)
        .success(function(data, status) {
          deferred.resolve(data);
        })
        .error(function(msg, code) {
          deferred.reject(msg);
          $log.error(msg, code);
        });;
      /** done with get request */
      return deferred.promise;
    };

    /**
     * Returns a promise to save the document in benchmark
     */
    function saveAsBenchmark(documentId) {
      var deferred = $q.defer();
      /** make a get request */
      $http.get(documentServiceBase + 'saveBenchmarkFile' + '?documentId=' + documentId)
        .success(function(data) {
          deferred.resolve(data);
        })
        .error(function(msg, code) {
          deferred.reject(msg);
          $log.error(msg, code);
        });;
      /** done with get request */
      return deferred.promise;
    };


    /**
     * Returns a promise to get benchmark score
     */
    function getBenchmarkScore(documentId) {
      var deferred = $q.defer();
      /** make a get request */
      $http.get(documentServiceBase + 'getBenchmarkScore?documentId=' + documentId)
        .success(function(data) {
          deferred.resolve(data);
        })
        .error(function(msg, code) {
          deferred.reject(msg);
          $log.error(msg, code);
        });;
      /** done with get request */
      return deferred.promise;
    };

    /**
     * Returns a promise to retrieves document ids
     **/
    function getDocumentIds() {
      var deferred = $q.defer();
      /** make a get request */
      $http.get(documentServiceBase + 'listDocs')
        .success(function(data, status) {
          deferred.resolve(data);
        })
        .error(function(msg, code) {
          deferred.reject(msg);
          $log.error(msg, code);
        });;
      /** done with get request */
      return deferred.promise;
    };


    /**
     * Returns a promise to load the document for a given id
     *
     * @return - Data contains html content of the document
     */
    function loadDocument(documentId) {
      var deferred = $q.defer();
      /** make a get request */
      $http.get(documentServiceBase + 'getDoc?documentId=' + documentId)
        .success(function(data, status) {
          deferred.resolve(data);
        })
        .error(function(msg, code) {
          deferred.reject(msg);
          $log.error(msg, code);
        });;
      /** done with get request */
      return deferred.promise;
    };

    /**
     * Returns a promise which will fetch the
     * index for a given document
     **/
    function getIndex(documentId) {
      var deferred = $q.defer();
      /** make a get request */
      $http.get(documentServiceBase + 'getIndex' + '?documentId=' + documentId)
        .success(function(data, status) {
          deferred.resolve(data);
        })
        .error(function(msg, code) {
          deferred.reject(msg);
          $log.error(msg, code);
        });;
      // done with get request
      return deferred.promise;
    }



  };
})();