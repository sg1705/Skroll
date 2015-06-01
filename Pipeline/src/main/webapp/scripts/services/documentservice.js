'use strict';

/**
 * @ngdoc service
 * @name myappApp.Documentservice
 * @description
 * # documentservice
 * Service in the myappApp.
 */
angular.module('SkrollApp')
  .service('documentService', function($http, $q, $log, LHSModel, documentModel) {
    //context root of API
    var documentServiceBase = 'restServices/doc/';
    var instrumentServiceBase = 'restServices/instrument/';
    /**
     * Retrieves terms for a given document
     */
    this.getTerms = function() {
      var deferred = $q.defer();
      /** make a get request */
      $http.get(documentServiceBase + 'getTerms' + '?documentId=' + documentModel.documentId)
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
     * Update terms for a given document
     */
    this.updateTerms = function(terms) {
      var deferred = $q.defer();
      /** make a get request */
      $http.post(documentServiceBase + 'updateTerms' + '?documentId=' + documentModel.documentId, terms)
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
     * Retrieves list of similar paragraphs
     */
    this.getSimilarPara = function(paragraphId) {
      $log.debug(paragraphId);
      var deferred = $q.defer();
      /** make a get request */
      $http.get(instrumentServiceBase + 'getSimilarPara?paragraphId=' +
          paragraphId + '&documentId=' + documentModel.documentId)
        .success(function(data) {
          var paragraphs = [];
          for (var ii = 0; ii < data.length; ii++) {
            var para = {};
            para.paragraphId = data[ii].map.IdAnnotation;
            para.definition = data[ii].map.TextAnnotation.substr(0, 12);
            paragraphs.push(para);
          }
          deferred.resolve(paragraphs);
        })
        .error(function(msg, status) {
          console.log(status);
          deferred.reject(msg)
        });
      /** done with get request */
      return deferred.promise;
    };

    /**
     * Retrieves Json for a given paragraph
     */
    this.getParagraphJson = function(paragraphId) {
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
    }

    /**
    * Removes all instances of paraId from smodel.terms and updates it
    */
    this.rejectClassFromPara = function(classId, paraId) {
      //get a filtered list
      var terms = LHSModel.filterOutClassFromPara(classId, paraId);
      return this.updateTerms(terms);
    }

    /**
    * Approves all instances of terms in the paraId for the given class
    */
    this.approveClassForPara = function(classId, paraId) {
      //get a filtered list
      var terms = LHSModel.getParagraphsForClass(classId, paraId);
      return this.updateTerms(terms);
    }

    /**
    * Approves all instances of terms in the paraId for the given class
    */
    this.addTermToPara = function(item) {
      //get a filtered list
      var terms = LHSModel.getParagraphsForClass(item.classificationId, item.paragraphId);
      if (terms == null)
        terms = [];
      terms.push(item);
      return this.updateTerms(terms).then(function(data) {
      }, function(data, status) {
        console.log(status);
      });
    }

    /**
     * Retrieves terms for a given document
     */
    this.getDocumentIds = function() {
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
     * Loads the document for a given id
     */
    this.loadDocument = function(documentId) {
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
     * Update model
     */
    this.updateModel = function() {
      /** make a get request */
      $http.get(documentServiceBase + 'updateModel' + '?documentId=' + documentModel.documentId)
        .success(function(data, status) {
          console.log("Updated model:");
          console.log(data);
        })
        .error(function(data, status) {
          console.log(status);
        });;
    };


    /**
     * Sets a given flag
     */
    this.setFlags = function(flagName, flagValue) {
      /** make a get request */
      $http.get(instrumentServiceBase + 'setFlags?flagName=' + flagName + '&flagValue=' + flagValue + '&documentId=' + documentModel.documentId)
        .success(function(data, status) {
          console.log("flag set");
          console.log(data);
        })
        .error(function(data, status) {
          console.log(status);
        });;
    };

    /**
     * Observe none
     */
    this.observeNone = function() {
      /** make a get request */
      $http.get(documentServiceBase + 'observeNone' + '?documentId=' + documentModel.documentId)
        .success(function(data, status) {
          console.log("observed none");
          console.log(data);
        })
        .error(function(data, status) {
          console.log(status);
        });;
    };


    /**
     * Get probability dump
     */
    this.getProbabilityDump = function() {
      var deferred = $q.defer();
      /** make a get request */
      $http.get(instrumentServiceBase + 'getProbabilityDump' + '?documentId=' + documentModel.documentId)
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
  });