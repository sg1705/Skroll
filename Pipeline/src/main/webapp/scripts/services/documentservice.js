'use strict';

/**
 * @ngdoc service
 * @name myappApp.Documentservice
 * @description
 * # documentservice
 * Service in the myappApp.
 */
angular.module('SkrollApp')
  .service('documentService', function ($http, $q, $log) {
    //context root of API
    var documentServiceBase = 'restServices/jsonAPI/';

    /**
    * Retrieves definitions for a given document
    */
    this.getDefinition = function() {
      var deferred = $q.defer();
      /** make a get request */
      $http.get(documentServiceBase + 'getDefinition')
        .success(function(data, status){
          var definitions = [ ];
          for(var ii = 0; ii < data.length; ii++) {
            // create definition object
            var def = {};
            def.paragraphId = data[ii].paragraphId;
            def.definition = data[ii].definedTerm;
            definitions.push(def);
          }         
          deferred.resolve(definitions);
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
        $http.get(documentServiceBase + 'getSimilarPara?paragraphId=' + paragraphId)
          .success(function(data) {
            var paragraphs = [ ];
            for(var ii = 0; ii < data.length; ii++) {
                var para = {};
                para.paragraphId = data[ii].map.IdAnnotation;
                para.definition = data[ii].map.TextAnnotation.substr(0,12);
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
      $http.get(documentServiceBase + 'getParagraphJson?paragraphId=' + paragraphId)
        .success(function(data) {
          deferred.resolve(data);
        })
        .error(function(msg, status) {
          deferred.reject(msg);
        })
      return deferred.promise;
    }

  });
