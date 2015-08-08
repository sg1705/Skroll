(function() {
  'use strict';


  angular
    .module('skroll.app.core.services', []);

  angular
    .module('skroll.app.core.services')
    .service('documentServiceNew', DocumentServiceNew);

  function DocumentServiceNew($http, $q, $log) {

    //-- private variables
    //context root of API
    var documentServiceBase   = 'restServices/doc/';
    var instrumentServiceBase = 'restServices/instrument/';


    //-- service definition
    var service = {
      
      getParagraphJson  : getParagraphJson,
      getTerms          : getTerms,
      unObserveTerms    : unObserveTerms,
      updateTerms       : updateTerms,

      getBenchmarkScore : getBenchmarkScore,
      saveAsBenchmark   : saveAsBenchmark
    };


    //////////////


    /**
    * Returns a promise to retrieves Json for a given paragraph
    **/
    function getParagraphJson(documentId, paragraphId) {
      $log.debug("Fetching json for paragraphId:" + paragraphId);
      var deferred = $q.defer();
      $http.get(instrumentServiceBase + 'getParagraphJson?paragraphId=' +
          paragraphId + '&documentId=' + documentModel.documentId)
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
    * Returns a promise to unobserve a paragraph
    **/
    function unObserveTerms(documentId, terms) {
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
    function getBenchmarkScore() {
      var deferred = $q.defer();
      /** make a get request */
      $http.get(documentServiceBase + 'getBenchmarkScore')
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
  };
})();