'use strict';

/**
 * @ngdoc service
 * @name myappApp.Documentservice
 * @description
 * # documentservice
 * Service in the myappApp.
 */
angular.module('SkrollApp')
  .service('documentService', function($http, $q, $log, LHSModel) {
    //context root of API
    var documentServiceBase = 'restServices/jsonAPI/';

    /**
     * Retrieves terms for a given document
     */
    this.getTerms = function() {
      var deferred = $q.defer();
      /** make a get request */
      $http.get(documentServiceBase + 'getTerms')
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
      $http.post(documentServiceBase + 'updateTerms', terms)
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
      $http.get(documentServiceBase + 'getSimilarPara?paragraphId=' +
          paragraphId)
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
      $http.get(documentServiceBase + 'getParagraphJson?paragraphId=' +
          paragraphId)
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
      this.updateTerms(terms).then(function(data) {
        return data;
      }, function(data, status) {
        console.log(status);
      });
    }

    /**
    * Approves all instances of terms in the paraId for the given class
    */
    this.approveClassForPara = function(classId, paraId) {
      //get a filtered list
      var terms = LHSModel.getParagraphsForClass(classId, paraId);
      this.updateTerms(terms).then(function(data) {
        return data;
      }, function(data, status) {
        console.log(status);
      });
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
      this.updateTerms(terms).then(function(data) {
        return data;
      }, function(data, status) {
        console.log(status);
      });
    }


  });